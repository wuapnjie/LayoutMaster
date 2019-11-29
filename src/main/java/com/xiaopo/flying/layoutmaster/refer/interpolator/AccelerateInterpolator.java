package com.xiaopo.flying.layoutmaster.refer.interpolator;

public class AccelerateInterpolator implements TimeInterpolator {
  private final float mFactor;
  private final double mDoubleFactor;

  public AccelerateInterpolator() {
    mFactor = 1.0f;
    mDoubleFactor = 2.0;
  }

  /**
   * Constructor
   *
   * @param factor Degree to which the animation should be eased. Seting
   * factor to 1.0f produces a y=x^2 parabola. Increasing factor above
   * 1.0f  exaggerates the ease-in effect (i.e., it starts even
   * slower and ends evens faster)
   */
  public AccelerateInterpolator(float factor) {
    mFactor = factor;
    mDoubleFactor = 2 * mFactor;
  }

  public float getInterpolation(float input) {
    if (mFactor == 1.0f) {
      return input * input;
    } else {
      return (float) Math.pow(input, mDoubleFactor);
    }
  }
}

