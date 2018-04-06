package com.xiaopo.flying.layoutmaster

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.concurrency.EdtExecutorService
import java.awt.GraphicsEnvironment
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

/**
 * @author wupanjie on 2018/4/6.
 */

abstract class FlyingAnimator @JvmOverloads constructor(
    private val myName: String,
    private val myTotalFrames: Int,
    private val myCycleDuration: Int,
    private val myRepeatable: Boolean,
    val isForward: Boolean = true) : Disposable {

  private var myTicker: ScheduledFuture<*>? = null

  private var myCurrentFrame: Int = 0
  private var myStartTime: Long = 0
  private var myStartDeltaTime: Long = 0
  private var myInitialStep: Boolean = false
  @Volatile
  var isDisposed: Boolean = false
    private set

  val isRunning: Boolean
    get() = myTicker != null

  init {

    reset()

    if (skipAnimation()) {
      animationDone()
    }
  }

  private fun onTick() {
    if (isDisposed) return

    if (myInitialStep) {
      myInitialStep = false
      myStartTime = System.currentTimeMillis() - myStartDeltaTime // keep animation state on suspend
      paint()
      return
    }

    val cycleTime = (System.currentTimeMillis() - myStartTime).toDouble()
    if (cycleTime < 0) return  // currentTimeMillis() is not monotonic - let's pretend that animation didn't changed

    var newFrame = (cycleTime * myTotalFrames / myCycleDuration).toLong()

    if (myRepeatable) {
      newFrame %= myTotalFrames.toLong()
    }

    if (newFrame == myCurrentFrame.toLong()) return

    if (!myRepeatable && newFrame >= myTotalFrames) {
      animationDone()
      return
    }

    myCurrentFrame = newFrame.toInt()

    paint()
  }

  private fun paint() {
    onValueUpdated(if (isForward) myCurrentFrame else myTotalFrames - myCurrentFrame - 1, myTotalFrames, myCycleDuration)
  }

  private fun animationDone() {
    stopTicker()

    if (!isDisposed) {
      SwingUtilities.invokeLater { this.onAnimateEnd() }
    }
  }

  private fun stopTicker() {
    if (myTicker != null) {
      myTicker!!.cancel(false)
      myTicker = null
    }
  }

  protected open fun onAnimateEnd() {

  }

  fun suspend() {
    myStartDeltaTime = System.currentTimeMillis() - myStartTime
    myInitialStep = true
    stopTicker()
  }

  fun resume() {
    if (isDisposed) {
      stopTicker()
      return
    }
    if (skipAnimation()) {
      animationDone()
      return
    }

    if (myCycleDuration == 0) {
      myCurrentFrame = myTotalFrames - 1
      paint()
      animationDone()
    } else if (myTicker == null) {
      myTicker = EdtExecutorService.getScheduledExecutorInstance().scheduleWithFixedDelay(object : Runnable {
        override fun run() {
          onTick()
        }

        override fun toString(): String {
          return "Scheduled " + this@FlyingAnimator
        }
      }, 0, (myCycleDuration * 1000 / myTotalFrames).toLong(), TimeUnit.MICROSECONDS)
    }
  }

  private fun skipAnimation(): Boolean {
    if (GraphicsEnvironment.isHeadless()) {
      return true
    }
    val app = ApplicationManager.getApplication()
    return app != null && app.isUnitTestMode
  }

  abstract fun onValueUpdated(frame: Int, totalFrames: Int, cycle: Int)

  override fun dispose() {
    stopTicker()
    isDisposed = true
  }

  fun reset() {
    myCurrentFrame = 0
    myStartDeltaTime = 0
    myInitialStep = true
  }

  override fun toString(): String {
    val future = myTicker
    return "Animator '" + myName + "' @" + System.identityHashCode(this) +
        if (future == null || future.isDone) " (stopped)" else " (running $myCurrentFrame/$myTotalFrames frame)"
  }
}
