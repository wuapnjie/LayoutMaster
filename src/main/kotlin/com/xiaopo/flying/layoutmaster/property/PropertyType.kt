package com.xiaopo.flying.layoutmaster.property

import com.xiaopo.flying.layoutmaster.AndroidColor

/**
 * @author wupanjie on 2018/4/4.
 */
abstract class PropertyType<out T> {

  abstract fun parse(s: String): T

  object Integer: PropertyType<Int>() {
    override fun parse(s: String): Int {
      return s.toInt()
    }
  }

  object Float: PropertyType<kotlin.Float>() {
    override fun parse(s: String): kotlin.Float {
      return s.toFloat()
    }
  }

  object Double: PropertyType<kotlin.Double>() {
    override fun parse(s: String): kotlin.Double {
      return s.toDouble()
    }
  }
  
  object Byte: PropertyType<kotlin.Byte>() {
    override fun parse(s: String): kotlin.Byte {
      return s.toByte()
    }
  }

  object Character: PropertyType<kotlin.Char>() {
    override fun parse(s: String): kotlin.Char {
      return s[0]
    }
  }

  object Long: PropertyType<kotlin.Long>() {
    override fun parse(s: String): kotlin.Long {
      return s.toLong()
    }
  }

  object Short: PropertyType<kotlin.Short>() {
    override fun parse(s: String): kotlin.Short {
      return s.toShort()
    }
  }

  object Color: PropertyType<Int>() {
    override fun parse(s: String): Int {
      return AndroidColor.parseColor(s)
    }
  }

  /**
   * layout parameter only support Integer type
   * refer : ViewDebug.class in Android SDK
   */
  object LAYOUT: PropertyType<Int>() {
    override fun parse(s: String): Int {
      return s.toInt()
    }
  }

  object Custom: PropertyType<String>() {
    
    val tryTypes = arrayListOf<PropertyType<*>>(
        EnumPropertyType.Boolean,
        Integer,
        Float,
        Double,
        Long,
        Short,
        Byte,
        Character
    )
    
    override fun parse(s: String): String {
      return s
    }
  }

}