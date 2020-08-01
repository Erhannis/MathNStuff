package com.erhannis.mathnstuff.splines;

import com.erhannis.mathnstuff.NVector;

public class ColorSpline {
  public long id;
  public ColorSolid space;
  public BezierSpline spline;

  public ColorSpline() {
    spline = new BezierSpline();
  }

  //TODO Maybe should skip alpha, unless we MEAN it
  public int InterpolateToARGB(double t) {
    double[] color = InterpolateToDARGB(t);
    //TODO Are we shortchanging 0xFF here?
    int r = (int) (0xFF * color[0]);
    int g = (int) (0xFF * color[1]);
    int b = (int) (0xFF * color[2]);
    return (int) ((0xFF000000) + (0x00010000 * r) + (0x00000100 * g) + (0x00000001 * b));
  }

  public double[] InterpolateToDARGB(double t) {
    double[] color = spline.Interpolate(t);
    switch (space) {
      case RGB_CUBE:
        double rf = BezierSpline.Clamp(color[0], 0, 1);
        double gf = BezierSpline.Clamp(color[1], 0, 1);
        double bf = BezierSpline.Clamp(color[2], 0, 1);
        return new double[]{1f, rf, gf, bf};
      case HSV_CUBE:
      case HSV_CYLINDER:
      case HSL_CUBE:
      case HSL_CYLINDER:
      case CIE_LAB:
      default:
        return new double[]{1f, 1f, 0f, 1f};
    }
  }

  /**
   * Use 3 dims, else undefined behavior
   *
   * @param points
   */
  public void SetPoints(double[][] points) {
    spline.SetPoints(points);
  }

  /**
   * Use 3 dims, else undefined behavior
   *
   * @param points
   */
  public void SetPoints(NVector[] points) {
    double[][] newPts = new double[3][points.length];
    for (int i = 0; i < points.length; i++) {
      double[] coords = points[i].coords;
      for (int j = 0; j < coords.length; j++) {
        newPts[j][i] = points[i].coords[j];
      }
    }
    spline.SetPoints(newPts);
  }

  public String ToJSON() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("id:" + id);
    sb.append(", ");
    sb.append("space:" + space); //TODO ToJSON?
    sb.append(", ");
    sb.append("spline:" + spline.ToJSON());
    sb.append("}");
    return sb.toString();
  }

  public static ColorSpline FromJSON(String json) {
    throw new RuntimeException("Not yet implemented");
  }
}
