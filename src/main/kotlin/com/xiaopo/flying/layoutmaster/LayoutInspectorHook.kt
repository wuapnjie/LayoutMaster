package com.xiaopo.flying.layoutmaster

import com.android.ddmlib.Client
import com.android.ddmlib.HandleViewDebug
import com.android.layoutinspector.model.ClientWindow
import com.android.layoutinspector.model.ViewNode
import com.android.layoutinspector.model.ViewProperty
import com.android.tools.idea.editors.layoutInspector.LayoutInspectorContext
import com.android.tools.idea.editors.layoutInspector.LayoutInspectorEditor
import com.android.tools.idea.editors.layoutInspector.ptable.LITableItem
import com.android.tools.idea.editors.layoutInspector.ui.ViewNodeActiveDisplay
import com.android.tools.property.ptable.PTable
import com.android.tools.property.ptable.PTableItem
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.xiaopo.flying.layoutmaster.property.FlyingProperty
import com.xiaopo.flying.layoutmaster.property.PropertyType
import com.xiaopo.flying.layoutmaster.property.SupportProperties
import com.xiaopo.flying.layoutmaster.ui.FlyingPopup
import org.joor.Reflect
import org.joor.ReflectException
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.IOException

/**
 * @author wupanjie on 2018/4/4.
 */
class LayoutInspectorHook(
    project: Project,
    layoutInspectorEditor: LayoutInspectorEditor,
    private val client: Client,
    window: ClientWindow
) {

  private var flyingPopup: FlyingPopup
  private var previewDisplay: ViewNodeActiveDisplay
  private var propertyTable: PTable
  private val windowTitle: String

  init {
    val editorReflect = Reflect.on(layoutInspectorEditor)
    //LayoutInspectorEditorPanel editorPanel = editorReflect.get("myPanel");
    val context = editorReflect.get<LayoutInspectorContext>("myContext")

    propertyTable = context.propertiesTable
    previewDisplay = Reflect.on(context).get<ViewNodeActiveDisplay>("myPreview")

    windowTitle = Reflect.on(window).get<String>("title")

    flyingPopup = FlyingPopup(project, propertyTable)
  }

  fun hook() {

    propertyTable.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(event: MouseEvent?) {
        if (event!!.clickCount == 2) {
          val row = propertyTable.rowAtPoint(event.point)
          if (row == -1) {
            return
          }

          val item = propertyTable.getValueAt(row, 0) as PTableItem

          if (item is LITableItem) {
            val propertyName = getPropertyName(item)
            val flyingProperty = determineFlyingProperty(item, propertyName) ?: return

            // show our popup ui
            flyingPopup.show(event, flyingProperty) { tItem, property, value ->
              onValueConfirmed(tItem, property, value)
            }
          }
        }
      }
    })
  }

  private fun determineFlyingProperty(item: PTableItem, propertyName: String): FlyingProperty? {
    return if (getPropertyGroupName(item) == "custom") {
      val itemName = item.name
      if (itemName.startsWith("layout_")) {
        FlyingProperty.customLayout(itemName, itemName.replaceFirst("layout_".toRegex(), ""))
      } else if (("false" == item.value) or ("true" == item.value)) {
        val method = "set" + itemName[0].toUpperCase() + itemName.substring(1)
        FlyingProperty.customBoolean(itemName, method)
      } else if (itemName.toLowerCase().contains("color")) {
        val method = "set" + itemName[0].toUpperCase() + itemName.substring(1)
        FlyingProperty.customColor(itemName, method)
      } else {
        val method = "set" + itemName[0].toUpperCase() + itemName.substring(1)
        FlyingProperty.customOf(itemName, method)
      }
    } else {
      SupportProperties[propertyName]
    }
  }

  fun onValueConfirmed(item: PTableItem, flyingProperty: FlyingProperty, value: String) {
    if (StringUtil.isEmpty(value)) {
      return
    }

    try {
      var selectedNode: ViewNode? = Reflect.on(previewDisplay).get<ViewNode>("mSelectedNode")
      if (selectedNode == null) {
        selectedNode = Reflect.on(previewDisplay).get<ViewNode>("mRoot")
      }

      selectedNode?.let {
        applyChanges(it, flyingProperty, value)
        onChangeApplied(item, it, value)
      }

    } catch (e: IOException) {
      e.printStackTrace()
    }

  }

  @Throws(IOException::class)
  private fun applyChanges(
      selectedNode: ViewNode,
      flyingProperty: FlyingProperty,
      changedValue: String) {

    when {
      (flyingProperty.type === PropertyType.LAYOUT) ->
        HandleViewDebug.setLayoutParameter(
            client,
            windowTitle,
            selectedNode.toString(),
            flyingProperty.parameter,
            PropertyType.LAYOUT.parse(changedValue))

      ("setPadding" == flyingProperty.method) ->
        applyPaddingChanges(selectedNode, flyingProperty, changedValue)

      flyingProperty.type === PropertyType.Custom ->
        tryAllTypes(selectedNode, flyingProperty, changedValue)

      else ->
        HandleViewDebug.invokeMethod(
            client,
            windowTitle,
            selectedNode.toString(),
            flyingProperty.method,
            flyingProperty.type.parse(changedValue))
    }
  }

  private fun tryAllTypes(
      selectedNode: ViewNode,
      flyingProperty: FlyingProperty,
      changedValue: String) {
    fun tryType(type: PropertyType<*>) {
      try {
        HandleViewDebug.invokeMethod(
            client,
            windowTitle,
            selectedNode.toString(),
            flyingProperty.method,
            type.parse(changedValue))
      } catch (e: Exception) {
        // do nothing
      }

    }

    PropertyType.Custom.tryTypes.forEach {
      tryType(it)
    }
  }

  private fun onChangeApplied(item: PTableItem, selectedNode: ViewNode, changedValue: String) {
    val propertyKey = getPropertyKey(item)

    val namedProperties = Reflect.on(selectedNode).get<MutableMap<String, ViewProperty>>("namedProperties")
    namedProperties[propertyKey]?.let { changedProperty ->
      try {
        Reflect.on(changedProperty).set("myValue", changedValue)
      }catch (e : ReflectException){
        Reflect.on(changedProperty).set("value", changedValue)
      }

      Reflect.on(item).set("myValue", changedValue)
      propertyTable.repaint()
    }
  }

  @Throws(IOException::class)
  private fun applyPaddingChanges(
      selectedNode: ViewNode,
      flyingProperty: FlyingProperty,
      changedValue: String) {

    val groupedProperties = Reflect.on(selectedNode).get<MutableMap<String, MutableList<ViewProperty>>>("groupedProperties")
    val paddingProperties = groupedProperties.getOrDefault("padding", arrayListOf())

    var paddingTop = ""
    var paddingLeft = ""
    var paddingBottom = ""
    var paddingRight = ""

    for (paddingProperty in paddingProperties) {
      val padding = try {
        Reflect.on(paddingProperty).get<String>("value")
      }catch (e : ReflectException){
        Reflect.on(paddingProperty).get<String>("myValue")
      }

      when (Reflect.on(paddingProperty).get<String>("name")) {
        "mPaddingLeft" -> paddingLeft = padding
        "mPaddingTop" -> paddingTop = padding
        "mPaddingRight" -> paddingRight = padding
        "mPaddingBottom" -> paddingBottom = padding
      }
    }

    when (flyingProperty.name) {
      "PaddingLeft" -> HandleViewDebug
          .invokeMethod(
              client,
              windowTitle,
              selectedNode.toString(),
              flyingProperty.method,
              flyingProperty.type.parse(changedValue),
              flyingProperty.type.parse(paddingTop),
              flyingProperty.type.parse(paddingRight),
              flyingProperty.type.parse(paddingBottom))
      "PaddingTop" -> HandleViewDebug
          .invokeMethod(
              client,
              windowTitle,
              selectedNode.toString(),
              flyingProperty.method,
              flyingProperty.type.parse(paddingLeft),
              flyingProperty.type.parse(changedValue),
              flyingProperty.type.parse(paddingRight),
              flyingProperty.type.parse(paddingBottom))
      "PaddingRight" -> HandleViewDebug
          .invokeMethod(
              client,
              windowTitle,
              selectedNode.toString(),
              flyingProperty.method,
              flyingProperty.type.parse(paddingLeft),
              flyingProperty.type.parse(paddingTop),
              flyingProperty.type.parse(changedValue),
              flyingProperty.type.parse(paddingBottom))
      "PaddingBottom" -> HandleViewDebug
          .invokeMethod(
              client,
              windowTitle,
              selectedNode.toString(),
              flyingProperty.method,
              flyingProperty.type.parse(paddingLeft),
              flyingProperty.type.parse(paddingTop),
              flyingProperty.type.parse(paddingRight),
              flyingProperty.type.parse(changedValue))
    }
  }

  private fun getPropertyKey(item: PTableItem): String {
    return if (item.parent != null &&
        item.parent!!.name != "methods" &&
        item.parent!!.name != "properties") {
      item.parent!!.name + ":" + item.name
    } else item.name
  }

  private fun getPropertyName(item: PTableItem): String {
    return item.name
  }

  private fun getPropertyGroupName(item: PTableItem): String {
    return item.parent?.name ?: ""
  }
}
