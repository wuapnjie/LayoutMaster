package com.xiaopo.flying.layoutmaster.property.animate

import com.xiaopo.flying.layoutmaster.property.PropertyType
import com.xiaopo.flying.layoutmaster.refer.interpolator.TimeInterpolator
import org.joor.Reflect

/**
 * @author wupanjie on 2018/4/7.
 */
data class InterpolatorInfo<T : TimeInterpolator>(
    val type: Class<T>,
    val parameters: LinkedHashMap<String, Pair<PropertyType<*>, *>>
) {
  companion object {
    fun <T : TimeInterpolator> of(
        type: Class<T>,
        parameters: LinkedHashMap<String, Pair<PropertyType<*>, *>> = LinkedHashMap()
    ): InterpolatorInfo<T> {
      return InterpolatorInfo(type, parameters)
    }
  }

  fun <T> createInterpolator(vararg args: Any?): T =
      if (args.isEmpty()) {
        Reflect.onClass(type)
            .create()
            .get()
      } else {
        Reflect.onClass(type)
            .create(*args)
            .get()
      }
}