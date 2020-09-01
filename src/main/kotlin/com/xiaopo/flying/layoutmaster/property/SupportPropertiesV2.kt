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
object SupportPropertiesV2 {
  private val ourSupportProperties = HashMap<String, FlyingProperty>()

  init {
    // scrolling
    ourSupportProperties["scrollX"] = of("ScrollX", "setScrollX", Integer)
    ourSupportProperties["scrollY"] = of("ScrollY", "setScrollY", Integer)

    // drawing
    ourSupportProperties["alpha"] = of("Alpha", "setAlpha", Float)
    ourSupportProperties["elevation"] = of("Elevation", "setElevation", Float)
    ourSupportProperties["pivotX"] = of("PivotX", "setPivotX", Float, false)
    ourSupportProperties["pivotY"] = of("PivotY", "setPivotY", Float, false)
    ourSupportProperties["rotation"] = of("Rotation", "setRotation", Float)
    ourSupportProperties["rotationX)"] = of("RotationX", "setRotationX", Float)
    ourSupportProperties["rotationY"] = of("RotationY", "setRotationY", Float)
    ourSupportProperties["scaleX"] = of("ScaleX", "setScaleX", Float)
    ourSupportProperties["scaleY"] = of("ScaleY", "setScaleY", Float)
    ourSupportProperties["transitionAlpha"] = of("TransitionAlpha", "setTransitionAlpha",
        Float)
    ourSupportProperties["translationX"] = of("TranslationX", "setTranslationX", Float)
    ourSupportProperties["translationY"] = of("TranslationY", "setTranslationY", Float)
    ourSupportProperties["translationZ"] = of("TranslationZ", "setTranslationZ", Float)
    ourSupportProperties["willNotDraw"] = of("WillNotDraw", "setWillNotDraw", Boolean)

    // 这个属性不是backgroundColor，但是这么用吧先
    ourSupportProperties["solidColor"] = of("BackgroundColor", "setBackgroundColor", Color)

    //layout
    ourSupportProperties["width"] = of("Width", "width", LAYOUT)
    ourSupportProperties["height"] = of("Height", "height", LAYOUT)
    ourSupportProperties["left"] = of("Left", "setLeft", Integer, false)
    ourSupportProperties["top"] = of("Top", "setTop", Integer, false)
    ourSupportProperties["right"] = of("Right", "setRight", Integer, false)
    ourSupportProperties["bottom"] = of("Bottom", "setBottom", Integer, false)
//    ourSupportProperties["layout_width"] = layout("Width", "width", LAYOUT)
//    ourSupportProperties["layout_height"] = layout("Height", "height", LAYOUT)
//    ourSupportProperties["layout_leftMargin"] = layout("LeftMargin", "leftMargin", LAYOUT)
//    ourSupportProperties["layout_topMargin"] = layout("TopMargin", "topMargin", LAYOUT)
//    ourSupportProperties["layout_rightMargin"] = layout("RightMargin", "rightMargin", LAYOUT)
//    ourSupportProperties["layout_bottomMargin"] = layout("BottomMargin", "bottomMargin", LAYOUT)
//    ourSupportProperties["layout_gravity"] = layout("BottomMargin", "bottomMargin", LAYOUT_GRAVITY)

    // text
    ourSupportProperties["curTextColor"] = of("TextColor", "setTextColor", Color)
    ourSupportProperties["scaledTextSize"] = of("TextSize", "setTextSize", Float)
    ourSupportProperties["textAlignment"] = of("TextAlignment", "setTextAlignment",
        TextAlignment)
    ourSupportProperties["textDirection"] = of("TextDirection", "setTextDirection",
        TextDirection)
    ourSupportProperties["gravity"] = of("Gravity", "setGravity", GRAVITY)

    // methods
    ourSupportProperties["fitsSystemWindows"] = of("FitsSystemWindows",
        "setFitsSystemWindows", Boolean)
    ourSupportProperties["activated"] = of("Activated", "setActivated", Boolean)
    ourSupportProperties["clickable"] = of("Clickable", "setClickable", Boolean)
    ourSupportProperties["enabled"] = of("Enabled", "setEnabled", Boolean)
    ourSupportProperties["hovered"] = of("Hovered", "setHovered", Boolean)
    ourSupportProperties["hapticFeedbackEnabled"] = of("HapticFeedbackEnabled",
        "setHapticFeedbackEnabled", Boolean)
    ourSupportProperties["selected"] = of("Selected", "setSelected", Boolean)
    ourSupportProperties["visibility"] = of("Visibility", "setVisibility", ViewVisibility)
    ourSupportProperties["soundEffectsEnabled"] = of("SoundEffectsEnabled",
        "setSoundEffectsEnabled", Boolean)

    // properties
//    ourSupportProperties["bg_state_mUseColor"] = of("BackgroundColor", "setBackgroundColor", Color)

    // padding
    ourSupportProperties["paddingTop"] = of("PaddingTop", "setPadding", Integer, false)
    ourSupportProperties["paddingLeft"] = of("PaddingLeft", "setPadding", Integer, false)
    ourSupportProperties["paddingBottom"] = of("PaddingBottom", "setPadding", Integer, false)
    ourSupportProperties["paddingRight"] = of("PaddingRight", "setPadding", Integer, false)

    // measurement
    ourSupportProperties["minWidth"] = of("MinWidth", "setMinimumWidth", Integer, false)
    ourSupportProperties["minHeight"] = of("MinHeight", "setMinimumHeight", Integer, false)
  }

  operator fun get(propertyName: String): FlyingProperty? {
    return ourSupportProperties[propertyName]
  }
}
