package com.xiaopo.flying.layoutmaster.property

/**
 * @author wupanjie on 2018/4/6.
 */
class BitwisePropertyType : EnumPropertyType<Int>() {

  override fun provideAvailableValues() {
    availableValues.put("LEFT", Gravity.LEFT)
    availableValues.put("TOP", Gravity.TOP)
    availableValues.put("RIGHT", Gravity.RIGHT)
    availableValues.put("BOTTOM", Gravity.BOTTOM)
    availableValues.put("CENTER_VERTICAL", Gravity.CENTER_VERTICAL)
    availableValues.put("FILL_VERTICAL", Gravity.FILL_VERTICAL)
    availableValues.put("CENTER_HORIZONTAL", Gravity.CENTER_HORIZONTAL)
    availableValues.put("FILL_HORIZONTAL", Gravity.FILL_HORIZONTAL)
    availableValues.put("CENTER", Gravity.CENTER)
    availableValues.put("FILL", Gravity.FILL)
    availableValues.put("CLIP_VERTICAL", Gravity.CLIP_VERTICAL)
    availableValues.put("CLIP_HORIZONTAL", Gravity.CLIP_HORIZONTAL)
    availableValues.put("START", Gravity.START)
    availableValues.put("END", Gravity.END)
  }

  override fun parse(s: String): Int {
    return s.toInt()
  }

  fun getValue(key: String): Int? {
    return availableValues.keyToValueMap[key]
  }

  companion object {

    val LAYOUT_GRAVITY = BitwisePropertyType()

    val GRAVITY = BitwisePropertyType()
  }
}
