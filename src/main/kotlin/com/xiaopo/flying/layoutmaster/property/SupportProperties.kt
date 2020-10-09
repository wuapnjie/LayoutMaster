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
    ourSupportProperties["mScrollX"] = of("ScrollX", "setScrollX", Integer)
    ourSupportProperties["mScrollY"] = of("ScrollY", "setScrollY", Integer)

    // drawing
    ourSupportProperties["getAlpha()"] = of("Alpha", "setAlpha", Float)
    ourSupportProperties["getElevation()"] = of("Elevation", "setElevation", Float)
    ourSupportProperties["getPivotX()"] = of("PivotX", "setPivotX", Float, false)
    ourSupportProperties["getPivotY()"] = of("PivotY", "setPivotY", Float, false)
    ourSupportProperties["getRotation()"] = of("Rotation", "setRotation", Float)
    ourSupportProperties["getRotationX()"] = of("RotationX", "setRotationX", Float)
    ourSupportProperties["getRotationY()"] = of("RotationY", "setRotationY", Float)
    ourSupportProperties["getScaleX()"] = of("ScaleX", "setScaleX", Float)
    ourSupportProperties["getScaleY()"] = of("ScaleY", "setScaleY", Float)
    ourSupportProperties["getTransitionAlpha()"] = of("TransitionAlpha", "setTransitionAlpha",
        Float)
    ourSupportProperties["getTranslationX()"] = of("TranslationX", "setTranslationX", Float)
    ourSupportProperties["getTranslationY()"] = of("TranslationY", "setTranslationY", Float)
    ourSupportProperties["getTranslationZ()"] = of("TranslationZ", "setTranslationZ", Float)
    ourSupportProperties["getX()"] = of("X", "setX", Float)
    ourSupportProperties["getY()"] = of("Y", "setY", Float)
    ourSupportProperties["getZ()"] = of("Z", "setZ", Float)
    ourSupportProperties["getClipChildren()"] = of("ClipChildren", "setClipChildren", Boolean)
    ourSupportProperties["getClipToPadding()"] = of("ClipToPadding", "setClipToPadding", Boolean)

    //layout
    ourSupportProperties["getWidth()"] = of("Width", "setWidth", Integer)
    ourSupportProperties["getHeight()"] = of("Height", "setHeight", Integer)
    ourSupportProperties["mLeft"] = of("Left", "setLeft", Integer, false)
    ourSupportProperties["mTop"] = of("Top", "setTop", Integer, false)
    ourSupportProperties["mRight"] = of("Right", "setRight", Integer, false)
    ourSupportProperties["mBottom"] = of("Bottom", "setBottom", Integer, false)
    ourSupportProperties["layout_width"] = layout("Width", "width", LAYOUT)
    ourSupportProperties["layout_height"] = layout("Height", "height", LAYOUT)
    ourSupportProperties["layout_leftMargin"] = layout("LeftMargin", "leftMargin", LAYOUT)
    ourSupportProperties["layout_topMargin"] = layout("TopMargin", "topMargin", LAYOUT)
    ourSupportProperties["layout_rightMargin"] = layout("RightMargin", "rightMargin", LAYOUT)
    ourSupportProperties["layout_bottomMargin"] = layout("BottomMargin", "bottomMargin", LAYOUT)
    ourSupportProperties["layout_gravity"] = layout("BottomMargin", "bottomMargin", LAYOUT_GRAVITY)

    // text
    ourSupportProperties["mCurTextColor"] = of("TextColor", "setTextColor", Color)
    ourSupportProperties["getScaledTextSize()"] = of("TextSize", "setTextSize", Float)
    ourSupportProperties["getRawTextAlignment()"] = of("TextAlignment", "setTextAlignment",
        TextAlignment)
    ourSupportProperties["getRawTextDirection()"] = of("TextDirection", "setTextDirection",
        TextDirection)
    ourSupportProperties["mGravity"] = of("Gravity", "setGravity", GRAVITY)

    // methods
    ourSupportProperties["getFitsSystemWindows()"] = of("FitsSystemWindows",
        "setFitsSystemWindows", Boolean)
    ourSupportProperties["isActivated()"] = of("Activated", "setActivated", Boolean)
    ourSupportProperties["isClickable()"] = of("Clickable", "setClickable", Boolean)
    ourSupportProperties["isEnabled()"] = of("Enabled", "setEnabled", Boolean)
    ourSupportProperties["isHovered()"] = of("Hovered", "setHovered", Boolean)
    ourSupportProperties["isHapticFeedbackEnabled()"] = of("HapticFeedbackEnabled",
        "setHapticFeedbackEnabled", Boolean)
    ourSupportProperties["isSelected()"] = of("Selected", "setSelected", Boolean)
    ourSupportProperties["getVisibility()"] = of("Visibility", "setVisibility", ViewVisibility)
    ourSupportProperties["isSoundEffectsEnabled()"] = of("SoundEffectsEnabled",
        "setSoundEffectsEnabled", Boolean)

    // properties
    ourSupportProperties["bg_state_mUseColor"] = of("BackgroundColor", "setBackgroundColor", Color)

    // padding
    ourSupportProperties["mPaddingTop"] = of("PaddingTop", "setPadding", Integer, false)
    ourSupportProperties["mPaddingLeft"] = of("PaddingLeft", "setPadding", Integer, false)
    ourSupportProperties["mPaddingBottom"] = of("PaddingBottom", "setPadding", Integer, false)
    ourSupportProperties["mPaddingRight"] = of("PaddingRight", "setPadding", Integer, false)

    // measurement
    ourSupportProperties["mMinWidth"] = of("MinWidth", "setMinimumWidth", Integer, false)
    ourSupportProperties["mMinHeight"] = of("MinHeight", "setMinimumHeight", Integer, false)
  }

  operator fun get(propertyName: String): FlyingProperty? {
    return ourSupportProperties[propertyName]
  }
}
