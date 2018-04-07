package com.xiaopo.flying.layoutmaster.property.animate

import android.animation.TimeInterpolator
import com.xiaopo.flying.layoutmaster.property.PropertyType
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
        Reflect.on(type)
            .create()
            .get()
      } else {
        Reflect.on(type)
            .create(*args)
            .get()
      }
}