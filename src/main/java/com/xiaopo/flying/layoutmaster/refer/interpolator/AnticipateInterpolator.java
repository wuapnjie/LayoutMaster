package com.xiaopo.flying.layoutmaster.refer.interpolator;

public class AnticipateInterpolator implements TimeInterpolator {
  private final float mTension;

  public AnticipateInterpolator() {
    mTension = 2.0f;
  }

  /**
   * @param tension Amount of anticipation. When tension equals 0.0f, there is
   * no anticipation and the interpolator becomes a simple
   * acceleration interpolator.
   */
  public AnticipateInterpolator(float tension) {
    mTension = tension;
  }

  public float getInterpolation(float t) {
    // a(t) = t * t * ((tension + 1) * t - tension)
    return t * t * ((mTension + 1) * t - mTension);
  }
}
