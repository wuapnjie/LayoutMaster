package com.xiaopo.flying.layoutmaster.refer.interpolator;

/**
 * @author wupanjie
 */
public class AccelerateDecelerateInterpolator implements TimeInterpolator {
  public AccelerateDecelerateInterpolator() {
  }

  public float getInterpolation(float input) {
    return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
  }
}

