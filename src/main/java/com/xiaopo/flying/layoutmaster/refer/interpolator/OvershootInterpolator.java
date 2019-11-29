package com.xiaopo.flying.layoutmaster.refer.interpolator;

public class OvershootInterpolator implements TimeInterpolator {
  private final float mTension;

  public OvershootInterpolator() {
    mTension = 2.0f;
  }

  /**
   * @param tension Amount of overshoot. When tension equals 0.0f, there is
   * no overshoot and the interpolator becomes a simple
   * deceleration interpolator.
   */
  public OvershootInterpolator(float tension) {
    mTension = tension;
  }

  public float getInterpolation(float t) {
    // _o(t) = t * t * ((tension + 1) * t + tension)
    // o(t) = _o(t - 1) + 1
    t -= 1.0f;
    return t * t * ((mTension + 1) * t + mTension) + 1.0f;
  }
}
