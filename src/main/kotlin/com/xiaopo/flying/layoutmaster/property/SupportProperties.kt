package com.xiaopo.flying.layoutmaster.property

import com.xiaopo.flying.layoutmaster.property.BitwisePropertyType.Companion.GRAVITY
import com.xiaopo.flying.layoutmaster.property.BitwisePropertyType.Companion.LAYOUT_GRAVITY
import com.xiaopo.flying.layoutmaster.property.EnumPropertyType.*
import com.xiaopo.flying.layoutmaster.property.EnumPropertyType.Boolean
import com.xiaopo.flying.layoutmaster.property.FlyingProperty.Companion.layout
import com.xiaopo.flying.layoutmaster.property.FlyingProperty.Companion.of
import com.xiaopo.flying.layoutmaster.property.PropertyType.*
import com.xiaopo.flying.layoutmaster.property.PropertyType.Float
import java.util.*


/**
 * @author wupanjie on 2018/4/4.
 */
object SupportProperties {
  private val ourSupportProperties = HashMap<String, FlyingProperty>()

  init {
    // scrolling
    ourSupportProperties["mScrollX"] = of("scrollX", "setScrollX", Integer)
    ourSupportProperties["mScrollY"] = of("scrollY", "setScrollY", Integer)

    // drawing
    ourSupportProperties["getAlpha()"] = of("alpha", "setAlpha", Float)
    ourSupportProperties["getElevation()"] = of("elevation", "setElevation", Float)
    ourSupportProperties["getPivotX()"] = of("pivotX", "setPivotX", Float)
    ourSupportProperties["getPivotY()"] = of("pivotY", "setPivotY", Float)
    ourSupportProperties["getRotation()"] = of("rotation", "setRotation", Float)
    ourSupportProperties["getRotationX()"] = of("rotationX", "setRotationX", Float)
    ourSupportProperties["getRotationY()"] = of("rotationY", "setRotationY", Float)
    ourSupportProperties["getScaleX()"] = of("scaleX", "setScaleX", Float)
    ourSupportProperties["getScaleY()"] = of("scaleY", "setScaleY", Float)
    ourSupportProperties["getTransitionAlpha()"] = of("transitionAlpha", "setTransitionAlpha", Float)
    ourSupportProperties["getTranslationX()"] = of("translationX", "setTranslationX", Float)
    ourSupportProperties["getTranslationY()"] = of("translationY", "setTranslationY", Float)
    ourSupportProperties["getTranslationZ()"] = of("translationZ", "setTranslationZ", Float)
    ourSupportProperties["getX()"] = of("x", "setX", Float)
    ourSupportProperties["getY()"] = of("y", "setY", Float)
    ourSupportProperties["getZ()"] = of("z", "setZ", Float)

    //layout
    ourSupportProperties["getWidth()"] = of("width", "setWidth", Integer)
    ourSupportProperties["getHeight()"] = of("height", "setHeight", Integer)
    ourSupportProperties["mLeft"] = of("left", "setLeft", Integer)
    ourSupportProperties["mTop"] = of("top", "setTop", Integer)
    ourSupportProperties["mRight"] = of("right", "setRight", Integer)
    ourSupportProperties["mBottom"] = of("bottom", "setBottom", Integer)
    ourSupportProperties["layout_width"] = layout("width", "width", LAYOUT)
    ourSupportProperties["layout_height"] = layout("height", "height", LAYOUT)
    ourSupportProperties["layout_leftMargin"] = layout("leftMargin", "leftMargin", LAYOUT)
    ourSupportProperties["layout_topMargin"] = layout("topMargin", "topMargin", LAYOUT)
    ourSupportProperties["layout_rightMargin"] = layout("rightMargin", "rightMargin", LAYOUT)
    ourSupportProperties["layout_bottomMargin"] = layout("bottomMargin", "bottomMargin", LAYOUT)
    ourSupportProperties["layout_gravity"] = layout("bottomMargin", "bottomMargin", LAYOUT_GRAVITY)

    // text
    ourSupportProperties["mCurTextColor"] = of("textColor", "setTextColor", Color)
    ourSupportProperties["getScaledTextSize()"] = of("textSize", "setTextSize", Float)
    ourSupportProperties["getRawTextAlignment()"] = of("textAlignment", "setTextAlignment", TextAlignment)
    ourSupportProperties["getRawTextDirection()"] = of("textDirection", "setTextDirection", TextDirection)
    ourSupportProperties["mGravity"] = of("gravity", "setGravity", GRAVITY)

    // methods
    ourSupportProperties["getFitsSystemWindows()"] = of("fitsSystemWindows",
        "setFitsSystemWindows", Boolean)
    ourSupportProperties["isActivated()"] = of("activated", "setActivated", Boolean)
    ourSupportProperties["isClickable()"] = of("clickable", "setClickable", Boolean)
    ourSupportProperties["isEnabled()"] = of("enabled", "setEnabled", Boolean)
    ourSupportProperties["isHovered()"] = of("hovered", "setHovered", Boolean)
    ourSupportProperties["isHapticFeedbackEnabled()"] = of("hapticFeedbackEnabled",
        "setHapticFeedbackEnabled", Boolean)
    ourSupportProperties["isSelected()"] = of("selected", "setSelected", Boolean)
    ourSupportProperties["getVisibility()"] = of("visibility", "setVisibility", ViewVisibility)
    ourSupportProperties["isSoundEffectsEnabled()"] = of("soundEffectsEnabled",
        "setSoundEffectsEnabled", Boolean)

    // properties
    ourSupportProperties["bg_state_mUseColor"] = of("backgroundColor", "setBackgroundColor", Color)

    // padding
    ourSupportProperties["mPaddingTop"] = of("paddingTop", "setPadding", Integer)
    ourSupportProperties["mPaddingLeft"] = of("paddingLeft", "setPadding", Integer)
    ourSupportProperties["mPaddingBottom"] = of("paddingBottom", "setPadding", Integer)
    ourSupportProperties["mPaddingRight"] = of("paddingRight", "setPadding", Integer)

    // measurement
    ourSupportProperties["mMinWidth"] = of("minWidth", "setMinimumWidth", Integer)
    ourSupportProperties["mMinHeight"] = of("minHeight", "setMinimumHeight", Integer)
  }

  operator fun get(propertyName: String): FlyingProperty? {
    return ourSupportProperties[propertyName]
  }
}
