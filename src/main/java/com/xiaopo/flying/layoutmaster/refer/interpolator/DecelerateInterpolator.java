package com.xiaopo.flying.layoutmaster.refer.interpolator;

/**
 * An interpolator where the rate of change starts out quickly and
 * and then decelerates.
 */
public class DecelerateInterpolator implements TimeInterpolator {
  public DecelerateInterpolator() {
  }

  /**
   * Constructor
   *
   * @param factor Degree to which the animation should be eased. Setting factor to 1.0f produces
   * an upside-down y=x^2 parabola. Increasing factor above 1.0f makes exaggerates the
   * ease-out effect (i.e., it starts even faster and ends evens slower)
   */
  public DecelerateInterpolator(float factor) {
    mFactor = factor;
  }

  public float getInterpolation(float input) {
    float result;
    if (mFactor == 1.0f) {
      result = (float) (1.0f - (1.0f - input) * (1.0f - input));
    } else {
      result = (float) (1.0f - Math.pow((1.0f - input), 2 * mFactor));
    }
    return result;
  }

  private float mFactor = 1.0f;
}

