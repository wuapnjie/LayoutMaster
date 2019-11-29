package com.xiaopo.flying.layoutmaster.ui

import com.android.tools.property.ptable.PTable
import com.android.tools.property.ptable.PTableItem
import com.intellij.ide.plugins.newui.ColorButton
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.ColorChooser
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBList
import com.intellij.ui.popup.AbstractPopup
import com.intellij.util.ui.JBUI
import com.xiaopo.flying.layoutmaster.AndroidColor
import com.xiaopo.flying.layoutmaster.FlyingAnimator
import com.xiaopo.flying.layoutmaster.property.BitwisePropertyType
import com.xiaopo.flying.layoutmaster.property.EnumPropertyType
import com.xiaopo.flying.layoutmaster.property.FlyingProperty
import com.xiaopo.flying.layoutmaster.property.PropertyType
import com.xiaopo.flying.layoutmaster.property.animate.SupportInterpolator
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.*
import kotlin.math.max
import kotlin.math.min

/**
 * @author wupanjie on 2018/4/5.
 */
typealias OnValueConfirmedListener =
    (item: PTableItem, flyingProperty: FlyingProperty, value: String) -> Unit

class FlyingPopup(private val project: Project, private val propertyTable: PTable) {
  private val minEnumPopupSize = Dimension(60, -1)
  private val minCommonSingleLinePopupSize = Dimension(360, -1)
  private val minCommonPopupSize = Dimension(360, 154)

  private val enumPropertyList = JBList<String>()

  private val commonPanel = JPanel()
  private val commonLayout = BoxLayout(commonPanel, BoxLayout.Y_AXIS)
  private val modifyPanel: JPanel = JPanel(BorderLayout())
  private val fromToPanel: JPanel = JPanel()
  private val interpolatorPanel: JPanel = JPanel(BorderLayout())
  private val startPanel: JPanel = JPanel(BorderLayout())

  private val modifyTextField: FlyingJTextField = createTextField()
  private val modifyConfirmButton: JButton = createConfirmButton()
  private val propertyLabel: JLabel = JLabel()
  private val toTextField: FlyingJTextField = FlyingJTextField()
  private val fromTextField: FlyingJTextField = FlyingJTextField()
  private val durationTextField: FlyingJTextField = FlyingJTextField(onlySupportInteger = true)
  private val interpolatorComboBox: ComboBox<String> = ComboBox()
  private val startButton = JButton()
  private val parametersPanel: FlyingParametersPanel = FlyingParametersPanel()

  private var lastProperty: FlyingProperty? = null

  private var animator: FlyingAnimator? = null

  init {
    initComponent()
  }

  private fun initComponent() {
    val cellRenderer = object : DefaultListCellRenderer() {
      override fun getListCellRendererComponent(list: JList<*>, value: Any?, index: Int,
                                                isSelected: Boolean, cellHasFocus: Boolean): Component {
        val c = super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus)
        border = JBUI.Borders.empty(4)
        return c
      }
    }
    cellRenderer.horizontalAlignment = SwingConstants.CENTER
    enumPropertyList.cellRenderer = cellRenderer

    // first
    modifyPanel.add(propertyLabel, BorderLayout.WEST)
    modifyPanel.add(modifyTextField, BorderLayout.CENTER)
    modifyPanel.add(modifyConfirmButton, BorderLayout.EAST)

    // second
    val fromToLayout = BoxLayout(fromToPanel, BoxLayout.X_AXIS)
    fromToPanel.layout = fromToLayout

    val fromLabel = JLabel("From: ")
    fromToPanel.add(fromLabel)
    fromToPanel.add(fromTextField)

    val toLabel = JLabel("To: ")
    fromToPanel.add(Box.createHorizontalStrut(4))
    fromToPanel.add(toLabel)
    fromToPanel.add(toTextField)

    val durationLabel = JLabel("Duration: ")
    fromToPanel.add(Box.createHorizontalStrut(4))
    fromToPanel.add(durationLabel)
    fromToPanel.add(durationTextField)

    // third
    val interpolatorLabel = JLabel("Interpolator: ")
    interpolatorComboBox.model = DefaultComboBoxModel(SupportInterpolator.keysArray)
    interpolatorPanel.add(interpolatorLabel, BorderLayout.WEST)
    interpolatorPanel.add(interpolatorComboBox, BorderLayout.CENTER)

    // forth
    startPanel.add(startButton, BorderLayout.CENTER)

    commonPanel.border = JBUI.Borders.empty(4)
  }

  private fun createConfirmButton(): JButton {
    val button = JButton()

    if (SystemInfo.isMac) {
      button.putClientProperty("JButton.buttonType", "text")
    }
    button.text = "Confirm"
    return button
  }

  private fun createTextField(): FlyingJTextField {
    val field = FlyingJTextField()
    field.addFocusListener(object : FocusAdapter() {
      override fun focusGained(e: FocusEvent?) {
        field.selectAll()
      }
    })
    return field
  }

  fun show(event: MouseEvent, flyingProperty: FlyingProperty, onValueConfirmedListener: OnValueConfirmedListener) {
    val row = propertyTable.rowAtPoint(event.point)
    val item = propertyTable.getValueAt(row, 0) as PTableItem

    when {
      flyingProperty.type is BitwisePropertyType ->
        showBitwisePropertyUI(event, flyingProperty, item, onValueConfirmedListener)
      flyingProperty.type is EnumPropertyType<*> ->
        showEnumPropertyUI(event, flyingProperty, item, onValueConfirmedListener)
      flyingProperty.type === PropertyType.Color ->
        showColorPropertyUI(flyingProperty, item, onValueConfirmedListener)
      else -> showCommonPropertyUI(event, flyingProperty, item, onValueConfirmedListener)
    }

    lastProperty = flyingProperty
  }

  private fun showBitwisePropertyUI(
      event: MouseEvent,
      flyingProperty: FlyingProperty,
      item: PTableItem,
      onValueConfirmedListener: OnValueConfirmedListener?) {
    val propertyType = flyingProperty.type as BitwisePropertyType
    val keys = (flyingProperty.type as EnumPropertyType<*>).keys()

    val borderLayout = BorderLayout(0, 4)
    val bitwisePanel = JPanel(borderLayout)
    bitwisePanel.border = JBUI.Borders.empty(4)
    val selectedPanel = JPanel(GridLayout(keys.size / 2, 2))
    val checkBoxes = ArrayList<JCheckBox>(keys.size)

    bitwisePanel.add(selectedPanel, BorderLayout.CENTER)
    for (key in keys) {
      val checkBox = JCheckBox(key)
      checkBoxes.add(checkBox)
      selectedPanel.add(checkBox)
    }

    val button = createConfirmButton()
    bitwisePanel.add(button, BorderLayout.SOUTH)

    val popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(bitwisePanel, null)
        .setCancelOnClickOutside(true)
        .setModalContext(false)
        .setMinSize(minEnumPopupSize)
        .setRequestFocus(true)
        .createPopup() as AbstractPopup

    popup.showInScreenCoordinates(propertyTable, calculatePopupLocation(event, popup))
    val focusManager = IdeFocusManager.getInstance(project)
    focusManager.requestFocus(selectedPanel, true)

    button.action = object : AbstractAction("Confirm") {
      override fun actionPerformed(e: ActionEvent) {
        var result = 0
        for (checkBox in checkBoxes) {
          if (checkBox.isSelected) {
            val value = propertyType.getValue(checkBox.text) ?: continue
            result = if (result == 0) {
              value
            } else {
              result or value
            }
          }
        }

        // if animator is running, stop it
        animator?.suspend()
        popup.cancel()
        onValueConfirmedListener?.invoke(item, flyingProperty, result.toString() + "")
      }
    }
  }

  private fun showCommonPropertyUI(
      event: MouseEvent,
      flyingProperty: FlyingProperty,
      item: PTableItem,
      onValueConfirmedListener: OnValueConfirmedListener?) {
    configCommonPanel(flyingProperty, item)

    val popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(commonPanel, null)
        .setCancelOnClickOutside(true)
        .setModalContext(false)
        .setMinSize(if (flyingProperty.supportAnimate) minCommonPopupSize else minCommonSingleLinePopupSize)
        .setRequestFocus(true)
        .createPopup() as AbstractPopup

    popup.showInScreenCoordinates(propertyTable, calculatePopupLocation(event, popup))
    val focusManager = IdeFocusManager.getInstance(project)
    focusManager.requestFocus(modifyTextField, true)

    configCommonAction(popup, onValueConfirmedListener, item, flyingProperty)

  }

  private fun configCommonAction(popup: AbstractPopup, onValueConfirmedListener: OnValueConfirmedListener?, item: PTableItem, flyingProperty: FlyingProperty) {
    interpolatorComboBox.setItemListener { itemEvent ->
      SupportInterpolator.getInterpolatorInfo(itemEvent.item as String)?.let { info ->
        val size = Dimension(commonPanel.width - 8, (commonPanel.height - 8) / 5)
        if (info.parameters.isNotEmpty()) {
          commonPanel.removeAll()
          commonPanel.add(modifyPanel)
          commonPanel.add(fromToPanel)
          commonPanel.add(interpolatorPanel)
          parametersPanel.apply {
            interpolatorInfo = info
            panel.size = size
            panel.doLayout()
            commonPanel.add(panel)
          }
          commonPanel.add(startPanel)
        } else {
          commonPanel.removeAll()
          parametersPanel.interpolatorInfo = info
          commonPanel.add(modifyPanel)
          commonPanel.add(fromToPanel)
          commonPanel.add(interpolatorPanel)
          commonPanel.add(startPanel)
        }

        popup.component?.doLayout()
      }
    }

    val confirmAction = object : AbstractAction("Confirm") {
      override fun actionPerformed(e: ActionEvent) {
        popup.cancel()
        onValueConfirmedListener?.invoke(item, flyingProperty, modifyTextField.text)
      }
    }
    modifyConfirmButton.action = confirmAction
    modifyTextField.action = confirmAction

    startButton.action = object : AbstractAction("Start") {
      override fun actionPerformed(e: ActionEvent?) {
        try {
          val fromValue = fromTextField.text.toFloat()
          val toValue = toTextField.text.toFloat()
          val durationMs = durationTextField.text.toLong()

          // stop previous
          animator?.suspend()

          //        val interpolatorExpression = interpolatorTextField.text
          val interpolator = SupportInterpolator
              .getInterpolator(interpolatorComboBox.selectedItem as String, parametersPanel.getParameters())

          animator = object : FlyingAnimator(
              "FlyingAnimator",
              durationMs.toInt() / 16,
              durationMs.toInt(),
              false
          ) {
            override fun onValueUpdated(frame: Int, totalFrames: Int, cycle: Int) {
              var t = frame.toFloat() / totalFrames.toFloat()
              //                (AviatorEvaluator.exec(interpolatorExpression, t) as? Double ?: t)
              //                    .clamp(0.0, 1.0)
              t = (interpolator?.getInterpolation(t) ?: t).clamp(0f, 1f)
              //            println("t : $t , interpolator : $interpolator")
              val currentValue: Float =
                  fromValue + t * (toValue - fromValue)

              onValueConfirmedListener?.invoke(item, flyingProperty, "$currentValue")
            }

            override fun onAnimateEnd() {
              onValueConfirmedListener?.invoke(item, flyingProperty, toTextField.text)
              animator = null
            }
          }

          animator?.resume()
        } catch (e: Exception) {
          Logger.getInstance("Please report to me").error(e)
        }
      }
    }
  }

  private fun configCommonPanel(flyingProperty: FlyingProperty, item: PTableItem) {
    commonPanel.removeAll()

    commonPanel.layout = commonLayout

    commonPanel.add(modifyPanel)
    if (flyingProperty.supportAnimate) {
      commonPanel.add(fromToPanel)
      commonPanel.add(interpolatorPanel)
      SupportInterpolator.getInterpolatorInfo(interpolatorComboBox.selectedItem as String)?.let { info ->
        parametersPanel.interpolatorInfo = info
        if (info.parameters.isNotEmpty()) {
          parametersPanel.apply {
            commonPanel.add(panel)
          }
        }
      }
      commonPanel.add(startPanel)

      if (lastProperty != flyingProperty) {
        fromTextField.text = ""
        toTextField.text = ""
        durationTextField.text = ""
      }
      interpolatorComboBox.selectedIndex = 0
    }

    propertyLabel.text = flyingProperty.name + ": "
    modifyTextField.text = item.value

    val isOnlySupportInteger =
        flyingProperty.type == PropertyType.Integer ||
            flyingProperty.type == PropertyType.Short ||
            flyingProperty.type == PropertyType.Long
    modifyTextField.onlySupportInteger = isOnlySupportInteger
    fromTextField.onlySupportInteger = isOnlySupportInteger
    toTextField.onlySupportInteger = isOnlySupportInteger
  }

  fun Float.clamp(lower: Float, upper: Float): Float = min(upper, max(lower, this))

  private fun showColorPropertyUI(
      flyingProperty: FlyingProperty,
      item: PTableItem,
      onValueConfirmedListener: OnValueConfirmedListener?) {

    val currentColor: Color = item.value?.toColor() ?: Color.BLACK
    val selectedColor = ColorChooser.chooseColor(
        propertyTable,
        "Select Color for " + flyingProperty.name,
        currentColor)

    if (selectedColor != null &&
        selectedColor != currentColor) {
      // if animator is running, stop it
      animator?.suspend()

      onValueConfirmedListener?.invoke(
          item,
          flyingProperty,
          AndroidColor.toHexColor(selectedColor.rgb))
    }
  }

  private fun showEnumPropertyUI(
      event: MouseEvent,
      flyingProperty: FlyingProperty,
      item: PTableItem,
      onValueConfirmedListener: OnValueConfirmedListener?) {

    val keys = (flyingProperty.type as EnumPropertyType<*>).keys()
    val listComboBoxModel = ListComboBoxModel(keys)
    var currentIndex = 0
    for (i in keys.indices) {
      if (keys[i] == item.value) {
        currentIndex = i
        break
      }
    }

    @Suppress("UNCHECKED_CAST")
    enumPropertyList.model = (listComboBoxModel as ListModel<String>)
    val popup = JBPopupFactory.getInstance()
        .createListPopupBuilder(enumPropertyList)
        .setCancelOnClickOutside(true)
        .setModalContext(false)
        .setRequestFocus(true)
        .setMinSize(minEnumPopupSize)
        .setItemChoosenCallback {
          // if animator is running, stop it
          animator?.suspend()

          val value = enumPropertyList.selectedValue
          if (value != null) {
            IdeFocusManager.getGlobalInstance().requestFocus(enumPropertyList, true)
            onValueConfirmedListener?.invoke(item, flyingProperty, value)
          }
        }
        .createPopup()

    enumPropertyList.selectedIndex = currentIndex

    popup.showInScreenCoordinates(propertyTable, calculatePopupLocation(event,
        popup as AbstractPopup))
    val focusManager = IdeFocusManager.getInstance(project)
    focusManager.requestFocus(enumPropertyList, true)
  }

  private fun calculatePopupLocation(event: MouseEvent, popup: AbstractPopup): Point {
    val row = propertyTable.rowAtPoint(event.point)
    val selectedCellRightRect = propertyTable.getCellRect(row, 0, true)
    val relativePoint = RelativePoint(event)
    val point = relativePoint.screenPoint
    val dimension = popup.content.preferredSize
    point.translate(-dimension.width / 2, -(dimension.height + selectedCellRightRect
        .height / 2))

    return point
  }

  private fun String.toColor(): Color {
    val colorInt: Int = try {
      Integer.parseInt(this)
    } catch (e: Exception) {
      AndroidColor.parseColor(this)
    }

    return Color.decode(colorInt.toString() + "")
  }

  private fun ComboBox<*>.setItemListener(itemListener: (ItemEvent) -> Unit) {
    // remove previous
    itemListeners.forEach {
      removeItemListener(it)
    }
    addItemListener {
      itemListener.invoke(it)
    }
  }
}


