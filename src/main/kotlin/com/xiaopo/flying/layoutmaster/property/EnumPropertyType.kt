package com.xiaopo.flying.layoutmaster.property

import com.xiaopo.flying.layoutmaster.BidirectionalMap
import java.util.*

/**
 * @author wupanjie on 2018/4/4.
 */
abstract class EnumPropertyType<T> : PropertyType<T?>() {

  protected var availableValues: BidirectionalMap<String, T> = BidirectionalMap()

  init {
    provideAvailableValues()
  }

  protected abstract fun provideAvailableValues()

  override fun parse(s: String): T? {
    return availableValues.keyToValueMap[s]
  }

  fun keys(): List<String> {
    return ArrayList(availableValues.keyToValueMap.keys)
  }

  object Boolean : EnumPropertyType<kotlin.Boolean>() {
    override fun provideAvailableValues() {
      availableValues.put("true", true)
      availableValues.put("false", false)
    }
  }

  object ViewVisibility : EnumPropertyType<Int>() {
    const val VISIBLE = 0x00000000

    const val INVISIBLE = 0x00000004

    const val GONE = 0x00000008

    override fun provideAvailableValues() {
      availableValues.put("VISIBLE", VISIBLE)
      availableValues.put("INVISIBLE", INVISIBLE)
      availableValues.put("GONE", GONE)
    }
  }

  object TextAlignment : EnumPropertyType<Int>() {
    const val TEXT_ALIGNMENT_GRAVITY = 1

    const val TEXT_ALIGNMENT_TEXT_START = 2

    const val TEXT_ALIGNMENT_TEXT_END = 3

    const val TEXT_ALIGNMENT_CENTER = 4

    const val TEXT_ALIGNMENT_VIEW_START = 5

    const val TEXT_ALIGNMENT_VIEW_END = 6

    override fun provideAvailableValues() {
      availableValues.put("GRAVITY", TEXT_ALIGNMENT_GRAVITY)
      availableValues.put("TEXT_START", TEXT_ALIGNMENT_TEXT_START)
      availableValues.put("TEXT_END", TEXT_ALIGNMENT_TEXT_END)
      availableValues.put("CENTER", TEXT_ALIGNMENT_CENTER)
      availableValues.put("VIEW_START", TEXT_ALIGNMENT_VIEW_START)
      availableValues.put("VIEW_END", TEXT_ALIGNMENT_VIEW_END)
    }
  }

  object TextDirection : EnumPropertyType<Int>() {
    const val TEXT_DIRECTION_INHERIT = 0

    const val TEXT_DIRECTION_FIRST_STRONG = 1

    const val TEXT_DIRECTION_ANY_RTL = 2

    const val TEXT_DIRECTION_LTR = 3

    const val TEXT_DIRECTION_RTL = 4

    const val TEXT_DIRECTION_LOCALE = 5

    const val TEXT_DIRECTION_FIRST_STRONG_LTR = 6

    const val TEXT_DIRECTION_FIRST_STRONG_RTL = 7

    override fun provideAvailableValues() {
      availableValues.put("INHERIT", TEXT_DIRECTION_INHERIT)
      availableValues.put("FIRST_STRONG", TEXT_DIRECTION_FIRST_STRONG)
      availableValues.put("ANY_RTL", TEXT_DIRECTION_ANY_RTL)
      availableValues.put("LTR", TEXT_DIRECTION_LTR)
      availableValues.put("RTL", TEXT_DIRECTION_RTL)
      availableValues.put("LOCALE", TEXT_DIRECTION_LOCALE)
      availableValues.put("FIRST_STRONG_LTR", TEXT_DIRECTION_FIRST_STRONG_LTR)
      availableValues.put("FIRST_STRONG_RTL", TEXT_DIRECTION_FIRST_STRONG_RTL)
    }
  }

}
