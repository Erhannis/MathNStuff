package com.erhannis.mathnstuff.splines;

//TODO Maybe a plain class
//TODO implement N-surfaces?  :)
public class BezierSpline {
  private int mOrder;
  //TODO Do
  //private bool mReuseEnds = true;
  // Or
  private int mPointReuseCount = 1;
  //TODO Reverse order could improve efficiency, 40% chance
  // dim, point
  private double[][] mPoints;

  /**
   * Currently [dim][point]
   */
  public void SetPoints(double[][] points) {
    int pts = points[0].length;
    if ((pts - mPointReuseCount) % (mOrder + 1 - mPointReuseCount) != 0) {
      throw new IllegalArgumentException("invalid # of points for order " + mOrder + " curve with " + mPointReuseCount + " point reuse");
    }
    int segs = (pts - mPointReuseCount) / (mOrder + 1 - mPointReuseCount);
    //Debug.Log("Segments: " + segs);
    this.mPoints = points;
  }

  /**
   * 2 for quadratic, 3 for cubic, etc.
   */
  public void SetOrder(int order) {
    this.mOrder = order;
  }

  public double[] Interpolate(double t) {
    //TODO Clamp?
    //TODO Optimize?
    int segs = (mPoints[0].length - mPointReuseCount) / (mOrder + 1 - mPointReuseCount);
    int seg = (int) Math.floor(segs * t);
    //TODO Make better
    if (t == 1) {
      seg = segs - 1;
    }
    double width = 1f / segs;
    t = (t - (width * seg)) / width;
    int i = seg * (mOrder + 1 - mPointReuseCount);
    // i represents current curve, as an offset
    double[][] curPoints = ReduceOrder(mPoints, i, mOrder + 1, t);
    for (int j = 0; j < (mOrder - 1); j++) {
      curPoints = ReduceOrder(curPoints, 0, mOrder - j, t);
    }
    double[] result = new double[curPoints.length];
    for (int d = 0; d < curPoints.length; d++) {
      result[d] = curPoints[d][0];
    }
    return result;
  }

  /**
   * [dim][points], t returns kinda [dim][points - 1], having been interpolated
   *
   * except, I added offset and count.
   */
  private static double[][] ReduceOrder(double[][] points, int offset, int count, double t) {
    if (offset == -1) {
      offset = 0;
    }
    if (count == -1) {
      count = points[0].length;
    }
    int dims = points.length;
    double[][] result = new double[dims][count - 1];
    //TODO Optimize?
    for (int i = offset; i < (offset + count - 1); i++) {
      for (int d = 0; d < dims; d++) {
        result[d][i - offset] = (points[d][i] * (1 - t)) + (points[d][i + 1] * t);
      }
    }
    return result;
  }

  public static double Clamp(double x, double min, double max) {
    return Math.max(Math.min(x, max), min);
  }

  public String ToJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("mOrder:" + mOrder);
    sb.append(", ");
    sb.append("mPointReuseCount:" + mPointReuseCount);
    sb.append(", ");
    sb.append("mPoints:");
    sb.append("[");
    //TODO Hmm.  Maybe I should consider flipping order.
    for (int dim = 0; dim < 3; dim++) {
      if (dim > 0) {
        sb.append(", ");
      }
      sb.append("[");
      for (int point = 0; point < mPoints[0].length; point++) {
        if (point > 0) {
          sb.append(", ");
        }
        sb.append(mPoints[dim][point]);
      }
      sb.append("]");
    }
    sb.append("]");
    sb.append("}");

    return sb.toString();
  }

  public static BezierSpline FromJSON(String json) {
    throw new RuntimeException("Not yet implemented");
  }
}
