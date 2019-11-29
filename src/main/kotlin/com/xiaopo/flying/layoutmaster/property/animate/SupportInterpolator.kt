package com.xiaopo.flying.layoutmaster.property.animate

import com.xiaopo.flying.layoutmaster.property.PropertyType
import com.xiaopo.flying.layoutmaster.property.animate.InterpolatorInfo.Companion.of
import com.xiaopo.flying.layoutmaster.refer.interpolator.AccelerateDecelerateInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.AccelerateInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.AnticipateInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.AnticipateOvershootInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.BounceInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.CycleInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.DecelerateInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.FastOutLinearInInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.FastOutSlowInInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.LinearInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.LinearOutSlowInInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.OvershootInterpolator
import com.xiaopo.flying.layoutmaster.refer.interpolator.TimeInterpolator

/**
 * @author wupanjie on 2018/4/7.
 */
object SupportInterpolator {
  private val ourSupportInterpolator = LinkedHashMap<String, InterpolatorInfo<out TimeInterpolator>>()

  init {
    ourSupportInterpolator["DecelerateInterpolator"] =
        of(
          DecelerateInterpolator::class.java,
            linkedMapOf<String, Pair<PropertyType<*>, *>>(
                "Factor" to (PropertyType.Float to 1f)
            ))
    ourSupportInterpolator["LinearInterpolator"] = of(LinearInterpolator::class.java)
    ourSupportInterpolator["AccelerateDecelerateInterpolator"] = of(AccelerateDecelerateInterpolator::class.java)
    ourSupportInterpolator["AccelerateInterpolator"] =
        of(
          AccelerateInterpolator::class.java,
            linkedMapOf<String, Pair<PropertyType<*>, *>>(
                "Factor" to (PropertyType.Float to 1f)
            ))
    ourSupportInterpolator["LinearOutSlowInInterpolator"] = of(LinearOutSlowInInterpolator::class.java)
    ourSupportInterpolator["FastOutLinearInInterpolator"] = of(FastOutLinearInInterpolator::class.java)
    ourSupportInterpolator["FastOutSlowInInterpolator"] = of(FastOutSlowInInterpolator::class.java)
    ourSupportInterpolator["BounceInterpolator"] = of(BounceInterpolator::class.java)
    ourSupportInterpolator["CycleInterpolator"] =
        of(
          CycleInterpolator::class.java,
            linkedMapOf<String, Pair<PropertyType<*>, *>>(
                "Cycles" to (PropertyType.Float to 1f)
            ))
    ourSupportInterpolator["OvershootInterpolator"] =
        of(
          OvershootInterpolator::class.java,
            linkedMapOf<String, Pair<PropertyType<*>, *>>(
                "Tension" to (PropertyType.Float to 2f)
            ))
    ourSupportInterpolator["AnticipateInterpolator"] =
        of(
          AnticipateInterpolator::class.java,
            linkedMapOf<String, Pair<PropertyType<*>, *>>(
                "Tension" to (PropertyType.Float to 2f)
            ))
    ourSupportInterpolator["AnticipateOvershootInterpolator"] =
        of(
          AnticipateOvershootInterpolator::class.java,
            linkedMapOf<String, Pair<PropertyType<*>, *>>(
                "Tension" to (PropertyType.Float to 2f),
                "ExtraTension" to (PropertyType.Float to 1.5f)
            ))
//    ourSupportInterpolator["PathInterpolator"] =
//        of(PathInterpolator::class.java,
//            linkedMapOf<String, Pair<PropertyType<*>, *>>(
//                "X1" to (PropertyType.Float to 1f),
//                "Y1" to (PropertyType.Float to 1f),
//                "X2" to (PropertyType.Float to 1f),
//                "Y2" to (PropertyType.Float to 1f)
//            ))
  }

  // computed property
  val keysArray
    get() = ourSupportInterpolator.keys.toTypedArray()

  fun getInterpolator(interpolatorName: String, args: Array<Any?>? = null): TimeInterpolator? =
      if (args == null) {
        ourSupportInterpolator[interpolatorName]?.createInterpolator()
      } else {
        ourSupportInterpolator[interpolatorName]?.createInterpolator(*args)
      }

  fun getInterpolatorInfo(interpolatorName: String) = ourSupportInterpolator[interpolatorName]
}