package com.xiaopo.flying.layoutmaster.ui

import java.awt.event.KeyEvent
import javax.swing.JTextField

/**
 * Let it only support double number or Digital
 *
 * @author wupanjie on 2018/4/7.
 */
class FlyingJTextField(var onlySupportInteger: Boolean = false) : @JvmOverloads JTextField() {
  override fun processKeyEvent(e: KeyEvent) {
    if (e.keyCode == KeyEvent.VK_ENTER) {
      super.processKeyEvent(e)
      e.consume()
      return
    }

    val nextChar = e.keyChar
    val willBeText =
        if (selectedText != null && selectedText.isNotEmpty()) {
          val a = text.substring(0, selectionStart)
          val b = text.substring(selectionEnd, text.length)
          text.removeRange(selectionStart, selectionEnd)
          a + nextChar + b
        } else text + nextChar
    if (willBeText == "-") {
      super.processKeyEvent(e)
    } else {
      if (onlySupportInteger) {
        willBeText.toLongOrNull()?.let {
          if (nextChar.isDigit()) {
            super.processKeyEvent(e)
          }
        }
      } else {
        willBeText.toDoubleOrNull()?.let {
          if (nextChar.isDigit() || nextChar == '.') {
            super.processKeyEvent(e)
          }
        }
      }
    }
    e.consume()
  }
}