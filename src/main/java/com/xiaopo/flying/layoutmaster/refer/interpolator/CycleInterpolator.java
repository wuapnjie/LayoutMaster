package com.xiaopo.flying.layoutmaster.refer.interpolator;

public class CycleInterpolator implements TimeInterpolator {
  public CycleInterpolator(float cycles) {
    mCycles = cycles;
  }

  public float getInterpolation(float input) {
    return (float) (Math.sin(2 * mCycles * Math.PI * input));
  }

  private float mCycles;
}