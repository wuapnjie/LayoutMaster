package com.xiaopo.flying.layoutmaster.ui

import android.animation.TimeInterpolator
import android.view.animation.*

/**
 * @author wupanjie on 2018/4/7.
 */
object SupportInterpolator {
  private val ourSupportInterpolator = LinkedHashMap<String, TimeInterpolator>()

  init {
    ourSupportInterpolator["LinearInterpolator"] = LinearInterpolator()
    ourSupportInterpolator["AccelerateDecelerateInterpolator"] = AccelerateDecelerateInterpolator()
    ourSupportInterpolator["DecelerateInterpolator"] = DecelerateInterpolator()
    ourSupportInterpolator["AccelerateInterpolator"] = AccelerateInterpolator()
    ourSupportInterpolator["BounceInterpolator"] = BounceInterpolator()
//    ourSupportInterpolator["CycleInterpolator"] = CycleInterpolator()
    ourSupportInterpolator["OvershootInterpolator"] = OvershootInterpolator()
    ourSupportInterpolator["AnticipateInterpolator"] = AnticipateInterpolator()
    ourSupportInterpolator["AnticipateOvershootInterpolator"] = AnticipateOvershootInterpolator()
  }

  // computed property
  val keysArray
    get() = ourSupportInterpolator.keys.toTypedArray()

  operator fun get(interpolatorName: String): TimeInterpolator? {
    return SupportInterpolator.ourSupportInterpolator[interpolatorName]
  }
}