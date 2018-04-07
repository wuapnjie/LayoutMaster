package com.xiaopo.flying.layoutmaster.ui

import com.xiaopo.flying.layoutmaster.property.PropertyType
import com.xiaopo.flying.layoutmaster.property.animate.InterpolatorInfo
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * @author wupanjie on 2018/4/7.
 */
class FlyingParametersPanel {
  var interpolatorInfo: InterpolatorInfo<*>? = null
    set(value) {
      field = value
      panel.removeAll()
      parameterFields.clear()
      field?.parameters?.forEach { name, typeDefaultValuePair ->
        val parameterNameLabel = JLabel("$name: ")
        val isOnlySupportInteger =
            typeDefaultValuePair.first == PropertyType.Integer ||
                typeDefaultValuePair.first == PropertyType.Short ||
                typeDefaultValuePair.first == PropertyType.Long
        val parameterTextField = FlyingJTextField(isOnlySupportInteger)
        // default value
        parameterTextField.text = "${typeDefaultValuePair.second ?: 0}"
        panel.add(parameterNameLabel)
        panel.add(parameterTextField)
        parameterFields.add(name to parameterTextField)
      }
    }

  val panel = JPanel()
  val boxLayout = BoxLayout(panel, BoxLayout.X_AXIS)
  private val parameterFields = arrayListOf<Pair<String, JTextField>>()

  init {
    panel.layout = boxLayout
  }

  fun getParameters(): Array<Any?> {
    val parameters = arrayListOf<Any?>()
    parameterFields.forEach {
      parameters.add(interpolatorInfo?.parameters?.get(it.first)?.first?.parse(it.second.text))
    }

    return parameters.toTypedArray()
  }

}
