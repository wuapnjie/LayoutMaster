package com.xiaopo.flying.layoutmaster.ui

import com.android.tools.adtui.ptable.PTable
import com.android.tools.adtui.ptable.PTableItem
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.ColorChooser
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBList
import com.intellij.ui.popup.AbstractPopup
import com.intellij.util.ui.JBUI
import com.xiaopo.flying.layoutmaster.AndroidColor
import com.xiaopo.flying.layoutmaster.property.BitwisePropertyType
import com.xiaopo.flying.layoutmaster.property.EnumPropertyType
import com.xiaopo.flying.layoutmaster.property.FlyingProperty
import com.xiaopo.flying.layoutmaster.property.PropertyType
import org.jdesktop.swingx.combobox.ListComboBoxModel
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*

/**
 * @author wupanjie on 2018/4/5.
 */

typealias OnValueConfirmedListener =
    (item: PTableItem, flyingProperty: FlyingProperty, value: String) -> Unit

class FlyingPopup(private val project: Project, private val propertyTable: PTable) {
  private val minEnumPopupSize = Dimension(50, -1)
  private val minCommonPopupSize = Dimension(300, -1)

  private val enumPropertyList = JBList<String>()
  private val commonPanel = JPanel()
  private val commonEditor: JTextField = createTextField()
  private val confirmButton: JButton = createConfirmButton()
  private val propertyLabel: JLabel = JLabel()

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
    enumPropertyList.border = JBUI.Borders.empty()

    val borderLayout = BorderLayout(12, 0)
    commonPanel.layout = borderLayout
    commonPanel.border = JBUI.Borders.empty(4)
    commonPanel.add(propertyLabel, BorderLayout.WEST)
    commonPanel.add(commonEditor, BorderLayout.CENTER)
    commonPanel.add(confirmButton, BorderLayout.EAST)
  }

  private fun createConfirmButton(): JButton {
    val button = JButton()

    if (SystemInfo.isMac) {
      button.putClientProperty("JButton.buttonType", "text")
    }
    button.text = "Confirm"
    return button
  }

  private fun createTextField(): JTextField {
    val field = JTextField()
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

    if (flyingProperty.type is BitwisePropertyType) {
      showBitwisePropertyUI(event, flyingProperty, item, onValueConfirmedListener)
    } else if (flyingProperty.type is EnumPropertyType<*>) {
      showEnumPropertyUI(event, flyingProperty, item, onValueConfirmedListener)
    } else if (flyingProperty.type === PropertyType.Color) {
      showColorPropertyUI(flyingProperty, item, onValueConfirmedListener)
    } else {
      showCommonPropertyUI(event, flyingProperty, item, onValueConfirmedListener)
    }
  }

  private fun showBitwisePropertyUI(
      event: MouseEvent,
      flyingProperty: FlyingProperty,
      item: PTableItem,
      onValueConfirmedListener: OnValueConfirmedListener?) {
    val propertyType = flyingProperty.type as BitwisePropertyType
    val keys = (flyingProperty.type as EnumPropertyType<*>).keys()
    val currentValue = propertyType.parse(item.value!!)

    val borderLayout = BorderLayout(0, 4)
    val bitwisePanel = JPanel(borderLayout)
    bitwisePanel.border = JBUI.Borders.empty(4)
    val selectedPanel = JPanel(GridLayout(keys.size / 2, 2))
    val checkBoxes = ArrayList<JCheckBox>(keys.size)

    bitwisePanel.add(selectedPanel, BorderLayout.CENTER)
    for (key in keys) {
      val checkBox = JCheckBox(key)

      //if (propertyType.isBitwise(key, currentValue)) {
      //  checkBox.setSelected(true);
      //}
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

    button.action = object : ConfirmAction() {
      override fun actionPerformed(e: ActionEvent) {
        var result = 0
        for (checkBox in checkBoxes) {
          if (checkBox.isSelected) {
            val value = propertyType.getValue(checkBox.text) ?: continue
            if (result == 0) {
              result = value
            } else {
              result = result or value
            }
          }
        }

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

    propertyLabel.text = flyingProperty.name + ": "
    commonEditor.text = item.value

    val popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(commonPanel, null)
        .setCancelOnClickOutside(true)
        .setModalContext(false)
        .setMinSize(minCommonPopupSize)
        .setRequestFocus(true)
        .createPopup() as AbstractPopup

    popup.showInScreenCoordinates(propertyTable, calculatePopupLocation(event, popup))
    val focusManager = IdeFocusManager.getInstance(project)
    focusManager.requestFocus(commonEditor, true)

    confirmButton.action = object : ConfirmAction() {
      override fun actionPerformed(e: ActionEvent) {
        popup.cancel()
        onValueConfirmedListener?.invoke(item, flyingProperty, commonEditor.text)
      }
    }
  }

  private fun showColorPropertyUI(
      flyingProperty: FlyingProperty,
      item: PTableItem,
      onValueConfirmedListener: OnValueConfirmedListener?) {

    val currentColor = parseColor(item.value)
    val selectedColor = ColorChooser.chooseColor(
        propertyTable,
        "Select Color for " + flyingProperty.name,
        currentColor)

    if (selectedColor != null &&
        selectedColor != currentColor) {
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

  private fun parseColor(value: String?): Color {
    val colorInt: Int = try {
      Integer.parseInt(value!!)
    } catch (e: Exception) {
      AndroidColor.parseColor(value!!)
    }

    return Color.decode(colorInt.toString() + "")
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

  private abstract inner class ConfirmAction internal constructor() : AbstractAction("Confirm")
}
