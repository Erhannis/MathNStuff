package com.erhannis.mathnstuff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.swap;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;
import com.erhannis.networks.Edge;
import com.erhannis.networks.Network;
import com.erhannis.networks.Node;

/**
 *
 * @author mewer12
 */
public class MeMath {
    public static double sqr(double a) {
        return a * a;
    }
    
    /**
     * Checks to see if a and b are pretty equal - to "digits" binary "decimal" places, for simplicity.
     * My simplicity, anyway.
     * @param a
     * @param b
     * @param digits
     * @return 
     */
    public static boolean prettyEqual(double a, double b, int binDigits) {
        return (((int) (a * (1 << binDigits))) == ((int) (b * (1 << binDigits))));
    }

    /**
     * Checks to see if a is pretty much equal to 0 - to "digits" binary "decimal" places, for simplicity.
     * My simplicity, anyway.
     * @param a
     * @param digits
     * @return 
     */
    public static boolean prettyZero(double a, int binDigits) {
        return (((int) (a * (1 << binDigits))) == 0);
    }
    
    public static long factorial(int a) {
        if (a <= 0) {
            return 1;
        }
        long product = a;
        for (int i = a - 1; i > 1; i--) {
            product *= i;
        }
        return product;
    }
    
    public static int mod(int x, int n) {
        return ((x % n) + n) % n;
    }

    public static double mod(double x, double n) {
        return ((x % n) + n) % n;
    }

    public static double[] radialToRectangular(double radius, double angle, double radialVelocity, double angularVelocity) {
        return new double[]{Math.cos(angle) * radius,
                            Math.sin(angle) * radius,
                           (Math.cos(angle) * radialVelocity) - (Math.sin(angle) * angularVelocity * radius),
                           (Math.sin(angle) * radialVelocity) + (Math.cos(angle) * angularVelocity * radius)};
    }

    public static double[] rectangularToRadial(double xPos, double yPos, double xVelocity, double yVelocity) {
        double r = Math.sqrt((xPos * xPos) + (yPos * yPos));
        return new double[]{r, Math.atan2(yPos, xPos), (((yPos * yVelocity) + (xPos * xVelocity)) / r), (((xPos * yVelocity) - (yPos * xVelocity)) / (r*r))};
    }

    public static double[] radialToRectangular(double[] ravw) {
        return new double[]{Math.cos(ravw[1]) * ravw[0],
                            Math.sin(ravw[1]) * ravw[0],
                           (Math.cos(ravw[1]) * ravw[2]) - (Math.sin(ravw[1]) * ravw[3] * ravw[0]),
                           (Math.sin(ravw[1]) * ravw[2]) + (Math.cos(ravw[1]) * ravw[3] * ravw[0])};
    }

    public static double[] rectangularToRadial(double[] xyXY) {
        double r = Math.sqrt((xyXY[0] * xyXY[0]) + (xyXY[1] * xyXY[1]));
        return new double[]{r, Math.atan2(xyXY[1], xyXY[0]), (((xyXY[1] * xyXY[3]) + (xyXY[0] * xyXY[2])) / r), (((xyXY[0] * xyXY[3]) - (xyXY[1] * xyXY[2])) / (r*r))};
    }
    
    public static final double DEFAULT_DELTA_X = 1e-8;
    
    /**
     * dim starts at 0.
     * @param f
     * @param dim
     * @return 
     */
    public static DoubleFunction roughPartial(final DoubleFunction f, final int dim, final double h) {
        return new DoubleFunction() {
            @Override
            public double evaluate(NVector x) {
                NVector xph = x.copy();
                xph.coords[dim] += h;
                return (f.evaluate(xph) - f.evaluate(x)) / h;
            }
        };
    }

    /**
     * dim starts at 0.
     * @param f
     * @param dim
     * @return 
     */
    public static DoubleFunction roughPartial(final DoubleFunction f, final int dim) {
        return new DoubleFunction() {
            @Override
            public double evaluate(NVector x) {
                NVector xph = x.copy();
                xph.coords[dim] += DEFAULT_DELTA_X;
                return (f.evaluate(xph) - f.evaluate(x)) / DEFAULT_DELTA_X;
            }
        };
    }
    
    public static Function<NVector> roughGradient(final DoubleFunction f, final int dims, final double h) {
        final DoubleFunction[] dfs = new DoubleFunction[dims];
        for (int i = 0; i < dims; i++) {
            dfs[i] = roughPartial(f, i, h);
        }
        return new Function<NVector>() {
            @Override
            public NVector evaluate(NVector x) {
                NVector result = new NVector(dims);
                for (int i = 0; i < dims; i++) {
                    result.coords[i] = dfs[i].evaluate(x);
                }
                return result;
            }
        };
    }
    
    public static Function<NVector> roughGradient(final DoubleFunction f, final int dims) {
        return roughGradient(f, dims, DEFAULT_DELTA_X);
    }
    
    public static Function<Matrix> roughHessian(final DoubleFunction f, final int dims, final double h, final boolean assumeSymmetric) {
      final DoubleFunction[][] dfs = new DoubleFunction[dims][dims];
      if (assumeSymmetric) {
        for (int i = 0; i < dims; i++) {
          for (int j = i; j < dims; j++) {
            DoubleFunction df = roughPartial(roughPartial(f, i, h), j, h);
            dfs[i][j] = df;
            if (i != j) {
              dfs[j][i] = df;
            }
          }
        }
      } else {
        for (int i = 0; i < dims; i++) {
          for (int j = 0; j < dims; j++) {
            dfs[i][j] = roughPartial(roughPartial(f, i, h), j, h);
          }
        }
      }
      return new Function<Matrix>() {
        @Override
        public Matrix evaluate(NVector x) {
          Matrix result = new Matrix(dims, dims);
          if (assumeSymmetric) {
            for (int i = 0; i < dims; i++) {
              for (int j = i; j < dims; j++) {
                double value = dfs[i][j].evaluate(x);
                result.val[i][j] = value;
                if (i != j) {
                  result.val[j][i] = value;
                }
              }
            }
          } else {
            for (int i = 0; i < dims; i++) {
              for (int j = 0; j < dims; j++) {
                result.val[i][j] = dfs[i][j].evaluate(x);
              }
            }
          }
          return result;
        }
      };
    }

    public static Function<Matrix> roughHessian(final DoubleFunction f, final int dims) {
      return roughHessian(f, dims, DEFAULT_DELTA_X, true);
    }
    
    /**
     * Uses Newton's method (with approximated derivatives) to find a zero of the function.
     * @param f Function
     * @param dims f goes from R_dims to R_1
     * @param stepFactor Factor to apply to the step length
     * @param h Step size for approximating derivatives
     * @param acceptableError If it gets within this far of zero, stop
     * @return 
     */
    public static NVector newtonsMethod(final DoubleFunction f, final int dims, final NVector start, final double stepFactor, final double h, final double acceptableError) throws Exception {
      Function<Matrix> hessianFunc = roughHessian(f, dims, h, false);
      Function<NVector> gradientFunc = roughGradient(f, dims, h);
      NVector x = start.copy();
      double score = f.evaluate(x);
      double oldScore = score + (10 * acceptableError);
      
      int count = 0;
      while (Math.abs(score - oldScore) > acceptableError) {  //TODO Kinda wrong for small step size
        oldScore = score;
        System.out.println("step " + (count++) + " " + score + " " + x);
        x = x.minusB(hessianFunc.evaluate(x).invert().multV(gradientFunc.evaluate(x)).multS(stepFactor));
        score = f.evaluate(x);
      }
      
      return x;
    }
    
    public static NVector newtonsMethod(final DoubleFunction f, final int dims) throws Exception {
      return newtonsMethod(f, dims, new NVector(new double[]{1, 1}), 0.5, DEFAULT_DELTA_X, DEFAULT_DELTA_X);
      //return newtonsMethod(f, dims, NVector.zero(dims), 0.5, DEFAULT_DELTA_X, DEFAULT_DELTA_X);
    }
    
    public static NVector gradientDescent(final DoubleFunction f, final int dims, final NVector start, double stepFactor, final double h, final double acceptableError) {
      boolean adaptive = (stepFactor == 0);
      if (adaptive) {
        //TODO Tends not to actually converge...still can be helpful for getting near the answer from far away
        stepFactor = 0.5;
      }
      Function<NVector> gradientFunc = roughGradient(f, dims, h);
      NVector x = start.copy();
      double score = f.evaluate(x);
      double oldScore = score + (10 * acceptableError);
      
      int count = 0;
      while (Math.abs(score - oldScore) > acceptableError) {  //TODO Kinda wrong for small step size
        oldScore = score;
        if (count % 1000 == 0) {
          System.out.println("step " + (count) + " " + score + " " + x);
        }
        count++;
        x = x.minusB(gradientFunc.evaluate(x).multS(stepFactor));
        score = f.evaluate(x);
        if (adaptive) {
          //stepFactor = Math.min(0.5, Math.abs(score / (oldScore - score)) / 10);
          stepFactor = Math.min(10, Math.abs(score / (oldScore - score)) / 10);
        }
      }
      System.out.println("done " + (count++) + " " + score + " " + x);
      
      return x;
    }

    public static NVector gradientDescent(final DoubleFunction f, final int dims) throws Exception {
      return gradientDescent(f, dims, new NVector(new double[]{1, 1}), 0, DEFAULT_DELTA_X, DEFAULT_DELTA_X);
      //return gradientDescent(f, dims, NVector.zero(dims), 0.5, DEFAULT_DELTA_X, DEFAULT_DELTA_X);
    }
    
    public static Network netplotDFunction(DoubleFunction df, int points, double xMin, double xMax, double xScaling, double yScaling) {
        Network net = new Network();
        double xStep;
        if (points > 1) {
            xStep = (xMax - xMin) / (points - 1);
        } else {
            xStep = 1;
        }
        double x = xMin;
        Node prev = null;
        NVector xv = new NVector(new double[]{x});
        for (int i = 0; i < points; i++, x += xStep) {
            xv.coords[0] = x;
            Node newNode = new Node(i, x * xScaling, df.evaluate(xv) * -yScaling);
            net.nodes.add(newNode);
            if (prev != null) {
                Edge newEdge = new Edge(prev, newNode, 0, 0);
                net.edges.add(newEdge);
                prev.connections.add(newNode);
                prev.edgesOut.add(newEdge);
                newNode.edgesIn.add(newEdge);
            }
            prev = newNode;
        }
        return net;
    }
    
    public static RToRFunction roughDerivative(final RToRFunction f, final double h) {
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                double xh = x;
                xh += h;
                return (f.evaluate(xh) - f.evaluate(x)) / h;
            }
        };
    }

    public static RToRFunction roughDerivative(final RToRFunction f) {
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                double xh = x;
                xh += DEFAULT_DELTA_X;
                return (f.evaluate(xh) - f.evaluate(x)) / DEFAULT_DELTA_X;
            }
        };
    }

    public static Network netplotVectorField(Function<NVector> f, int xpoints, int ypoints, double xMin, double xMax, double yMin, double yMax, double xScaling, double yScaling, double xVScaling, double yVScaling) {
        Network net = new Network();
        double xStep;
        if (xpoints > 1) {
            xStep = (xMax - xMin) / (xpoints - 1);
        } else {
            xStep = 1;
        }
        double yStep;
        if (ypoints > 1) {
            yStep = (yMax - yMin) / (ypoints - 1);
        } else {
            yStep = 1;
        }
        double x = xMin;
        double y = yMin;
        NVector xyv = new NVector(new double[]{x, y});
        x = xMin;
        for (int i = 0; i < xpoints; i++) {
            y = yMin;
            for (int j = 0; j < ypoints; j++) {
                xyv.coords[0] = x;
                xyv.coords[1] = y;
                Node base = new Node(i, x * xScaling, y * -yScaling);
                NVector v = f.evaluate(xyv);
                Node arrowtip = new Node(i, ((x * xScaling) + (v.coords[0] * xVScaling)), ((y * -yScaling) + (v.coords[1] * -yVScaling)));
                net.nodes.add(base);
                net.nodes.add(arrowtip);
                Edge newEdge = new Edge(base, arrowtip, 0, 0);
                net.edges.add(newEdge);
                base.connections.add(base);
                base.edgesOut.add(newEdge);
                base.edgesIn.add(newEdge);
                y += yStep;
            }
            x += xStep;
        }
        return net;
    }

    public static String graph(int[] data, boolean fillLines) {
        StringBuilder sb = new StringBuilder();
        if (fillLines) {
            for (int i = 0; i < data.length; i++) {
                sb.append("|" + MeUtils.multiplyString("*", data[i]) + "\n");
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                if (data[i] > 0) {
                    sb.append("|" + MeUtils.multiplyString(" ", data[i] - 1) + "*\n");
                } else {
                    sb.append("|\n");
                }
            }
        }
        return sb.toString();
    }
    
    public static String graph(RToRFunction f, double min, double max, double scale, int points) {
        int[] data = new int[points];
        for (int i = 0; i < points; i++) {
            data[i] = ((int)(f.evaluate(((i * (max - min)) / (points - 1)) + min) * scale));
        }
        return graph(data, false);
    }
    
    public static String histogram(double[] data, double min, double max, int bins, int width, boolean catchTails) {
        int[] histogram = new int[bins];
        for (int i = 0; i < data.length; i++) {
            double p = ((data[i] - min) / (max - min));
            if (p < 1) {
                if (p >= 0) {
                    histogram[(int) (p * bins)]++;
                } else if (catchTails) {
                    histogram[0]++;
                }
            } else if (p == 1 || catchTails) {
                histogram[bins - 1]++;
            }
        }
        int maxval = 0;
        for (int i = 0; i < bins; i++) {
            if (histogram[i] > maxval) {
                maxval = histogram[i];
            }
        }
        for (int i = 0; i < bins; i++) {
            histogram[i] *= width;
            histogram[i] /= maxval;
        }
        return graph(histogram, true);
    }
    
    /**
     * I'll make this generic later.
     * @return 
     */
    public static double max(double[] array) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    public static int max(int[] array) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * I'll make this generic later.
     * @return 
     */
    public static double min(double[] array) {
        double min = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    public static int min(int[] array) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }
    
    /**
     * Really only accurate within [-2,2], at n=20.
     * @param x
     * @param n
     * @return 
     */
    public static double erf(double x, int n) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                // Add
                sum += (Math.pow(x, (2 * i) + 1) / (factorial(i) * ((2 * i) + 1)));
            } else {
                // Subtract
                sum -= (Math.pow(x, (2 * i) + 1) / (factorial(i) * ((2 * i) + 1)));
            }
        }
        return (2 / Math.sqrt(Math.PI)) * sum;   
    }

    /**
     * (was) Really only accurate within [-2,2].  Now uses different approximation algorithm.
     * @param x
     * @return 
     */
    public static double erf(double x) {
        double t = 1 / (1 + 0.5 * Math.abs(x));
        double tau = t * Math.exp(-(x*x) - 1.26551223 + 1.00002368*t + 0.37409196*Math.pow(t,2) + 0.09678418*Math.pow(t,3) - 0.18628806*Math.pow(t,4) + 0.27886807*Math.pow(t,5) - 1.13520398*Math.pow(t,6) + 1.48851587*Math.pow(t,7) - 0.82215223*Math.pow(t,8) + 0.17087277*Math.pow(t,9));
        if (x >= 0) {
            return 1 - tau;
        } else {
            return tau - 1;
        }        
    }
    
    /**
     * Normally I'd like to make this generic for N dimensions, but it's useful
     * enough like this that I don't care right now.  Assumes both input vectors
     * have 3 elements.
     * @param a
     * @param b
     * @return 
     */
    public static double[] crossProduct3d(double[] u, double[] v) {
        double[] w = new double[3];
        w[0] = u[1]*v[2] - u[2]*v[1];
        w[1] = u[2]*v[0] - u[0]*v[2];
        w[2] = u[0]*v[1] - u[1]*v[0];
        return w;
    }
    
    /**
     * Arbitrary n.
     * @param u 
     */
    public static void vectorNormalizeIP(double[] u) {
        double length = vectorLength(u);
        if (length == 0) {
            return;
        }
        for (int i = 0; i < u.length; i++) {
            u[i] /= length;
        }        
    }

    /**
     * Arbitrary n.
     * @param u 
     */
    public static double[] vectorNormalize(double[] u) {
        double[] result = new double[u.length];
        double length = vectorLength(u);
        if (length == 0) {
            result[0] = 1;
            return result;
        }
        for (int i = 0; i < u.length; i++) {
            result[i] = u[i] / length;
        }        
        return result;
    }
    
    /**
     * Arbitrary n.
     * @param u
     * @param v
     * @return 
     */
    public static double dotProduct(double[] u, double[] v) {
        double sum = 0;
        for (int i = 0; i < u.length; i++) {
            sum += u[i] * v[i];
        }
        return sum;
    }

    /**
     * Arbitrary n.
     * @param u
     * @param v
     * @return 
     */
    public static int dotProduct(int[] u, int[] v) {
        int sum = 0;
        for (int i = 0; i < u.length; i++) {
            sum += u[i] * v[i];
        }
        return sum;
    }
    
    /**
     * Arbitrary n.
     * @param u
     * @return 
     */
    public static double vectorLength(double[] u) {
        return Math.sqrt(dotProduct(u, u));
    }
    
    /**
     * Arbitrary n.  Returns pi/2 if either vector has length 0.
     * @param u
     * @param v
     * @return 
     */
    public static double vectorAngle(double[] u, double[] v) {
        double uLen = vectorLength(u);
        double vLen = vectorLength(v);
        if (uLen == 0 || vLen == 0) {
            return Math.PI / 2;
        } else {
            return Math.acos(dotProduct(u, v) / (uLen * vLen));
        }
    }
    
    /**
     * Reflects vector u across a normal n.  Note that both u and its reflection
     * are assumed to point outwards from the surface.  Crashes if |N|=0.
     * @param u
     * @param n
     * @return 
     */
    public static double[] vectorReflect(double[] u, double[] n) {
        double factor = 2 / vectorLength(n);
        
        double[] result = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            result[i] = factor * n[i] * dotProduct(n, u) - u[i];
        }
        
        return result;
    }

    /**
     * Projects vector u onto v.  Crashes if |v|=0.
     * @param u
     * @param n
     * @return 
     */
    public static double[] vectorProject(double[] u, double[] v) {
        double vLen = vectorLength(v);
        return vectorScale(v, dotProduct(v, u) / sqr(vLen));
    }
    
    public static void vectorScaleIP(double[] u, double alpha) {
        for (int i = 0; i < u.length; i++) {
            u[i] *= alpha;
        }
    }

    public static double[] vectorScale(double[] u, double alpha) {
        double[] result = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            result[i] = alpha * u[i];
        }
        return result;
    }
    
    public static double[] vectorAdd(double[] u, double[] v) {
        double[] result = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            result[i] = u[i] + v[i];
        }
        return result;
    }

    public static int[] vectorAdd(int[] u, int[] v) {
        int[] result = new int[u.length];
        for (int i = 0; i < u.length; i++) {
            result[i] = u[i] + v[i];
        }
        return result;
    }

    public static double vectorDistance(int[] u, int[] v) {
        double result = 0;
        for (int i = 0; i < u.length; i++) {
            result += sqr(u[i] - v[i]);
        }
        return Math.sqrt(result);
    }

    public static double vectorDistanceSqr(int[] u, int[] v) {
        double result = 0;
        for (int i = 0; i < u.length; i++) {
            result += sqr(u[i] - v[i]);
        }
        return result;
    }

    public static double vectorDistance(double[] u, double[] v) {
        double result = 0;
        for (int i = 0; i < u.length; i++) {
            result += sqr(u[i] - v[i]);
        }
        return Math.sqrt(result);
    }

    public static double vectorDistanceSqr(double[] u, double[] v) {
        double result = 0;
        for (int i = 0; i < u.length; i++) {
            result += sqr(u[i] - v[i]);
        }
        return result;
    }
    
    /**
     * u - v
     * @param u
     * @param v
     * @return 
     */
    public static double[] vectorSubtract(double[] u, double[] v) {
        double[] result = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            result[i] = u[i] - v[i];
        }
        return result;
    }
    
    /**
     * Interpolates angle-smoothly from u to v, alpha of the way.  alpha goes
     * from 0 to 1.  Does not yet yield the right magnitude; resulting vectors
     * are normalized (I think).
     * @param u
     * @param v
     * @param alpha
     * @return 
     */
    public static double[] vectorAngleInterpolate(double[] u, double[] v, double alpha) {
        double theta = vectorAngle(u, v);
        double[] I = vectorNormalize(u);
        double[] J = vectorNormalize(vectorSubtract(v, vectorProject(v, u)));
        double[] w = vectorAdd(vectorScale(I, Math.cos(alpha * theta)), vectorScale(J, Math.sin(alpha * theta)));
        double[] cp = {0, 0};
        cp[0] = dotProduct(u, w);
        cp[1] = dotProduct(v, w);
//        double[] x = vectorScale(vectorAdd(vectorScale(u, dotProduct(u, w)), vectorScale(v, dotProduct(v, w))), 1 / dotProduct(vectorAdd(u, v), w));
        double[] x = vectorAdd(vectorScale(u, cp[0] / (cp[0] + cp[1])), vectorScale(v, cp[0] / (cp[0] + cp[1])));
        //System.out.println("acc: " + vectorAngle(w, x));
        return w;
    }
    
    public static boolean divides(long top, long bottom) {
        return ((top % bottom) == 0);
    }
    
    public static double sum(double[] array) {
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static int sum(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    /**
     * Given `counts` full of nonnegative index counts, returns a random index.
     * (Like drawing from a bag, with replacement.)
     * I might want to do something aside from Random....
     * @param r
     * @param array
     * @return 
     */
    public static int pickOne(Random r, int[] counts) {
        int index = r.nextInt(sum(counts));
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > index) return i;
            index -= counts[i];
        }
        return -1;
    }
    
    public static int bound(int value, int min, int max) {
        if (value <= min) return min;
        if (value >= max) return max;
        return value;
    }

    public static float bound(float value, float min, float max) {
        if (value <= min) return min;
        if (value >= max) return max;
        return value;
    }
    
    public static double bound(double value, double min, double max) {
        if (value <= min) return min;
        if (value >= max) return max;
        return value;
    }
    
    public static class MatlabFunctions {
      public static ArrayList<Integer> randperm(int i) {
        return randperm(i, null);
      }
      
      public static ArrayList<Integer> randperm(int i, SmallRandom rnd) {
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int j = 1; j <= i; j++) {
          indices.add(j);
        }
        if (rnd == null) {
          Collections.shuffle(indices);
        } else {
          shuffle(indices, rnd);
        }
        return indices;
      }

    private static final int SHUFFLE_THRESHOLD        =    5;
      
    public static void shuffle(List<?> list, SmallRandom rnd) {
        int size = list.size();
        if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
            for (int i=size; i>1; i--)
                swap(list, i-1, rnd.nextInt(i));
        } else {
            Object arr[] = list.toArray();

            // Shuffle array
            for (int i=size; i>1; i--)
                swap(arr, i-1, rnd.nextInt(i));

            // Dump array back into list
            // instead of using a raw type here, it's possible to capture
            // the wildcard but it will require a call to a supplementary
            // private method
            ListIterator it = list.listIterator();
            for (int i=0; i<arr.length; i++) {
                it.next();
                it.set(arr[i]);
            }
        }
    }

    public static void swap(List<?> list, int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }
    
    private static void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
    
      public static ArrayList<ArrayList<Integer>> zeros(int y, int x) {
        return digitArray(y, x, 0);
      }

      public static ArrayList<ArrayList<Integer>> ones(int y, int x) {
        return digitArray(y, x, 1);
      }
      
      public static ArrayList<ArrayList<Integer>> digitArray(int y, int x, int digit) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        for (int j = 1; j <= y; j++) {
          ArrayList<Integer> row = new ArrayList<Integer>();
          for (int i = 1; i <= x; i++) {
            row.add(digit);
          }
          result.add(row);
        }
        return result;
      }
    }

    /**
     * Throws IllegalArgumentException if signum(f(left)) == signum(f(right)), unless one of
     * them yields 0.
     * @param fn
     * @param left
     * @param right
     * @param max_iter
     * @param tol
     * @return 
     */
  public static double fzero(RToRFunction fn, double left, double right, int max_iter, double tol) {
    if (right < left) {
      double bucket = left;
      left = right;
      right = bucket;
    }
    
    double lv = fn.evaluate(left);
    double rv = fn.evaluate(right);
    if (lv == 0) {
      return left;
    } else if (rv == 0) {
      return right;
    }
    if (Math.signum(lv) == Math.signum(rv)) {
      throw new IllegalArgumentException("Same sign");
    }
    
    int iter = 0;
    double nx = (left + right) / 2;
    double nv = -1;
    
    while (iter < max_iter) { // May technically do one extra iteration
      nv = fn.evaluate(nx);
      if (nv == 0) {
        return nx;
      } else if (Math.signum(nv) == Math.signum(lv)) {
        if (Math.abs(left - nx) <= tol) {
          return nx;
        }
        left = nx;
        //lv = fn.evaluate(left);
      } else {
        if (Math.abs(right - nx) <= tol) {
          return nx;
        }
        right = nx;
        //rv = fn.evaluate(right);
      }
      nx = (left + right) / 2;
      
      iter++;
    }
    return nx;
  }

  public static boolean nearerFirst(double a, double b, double c) {
    //TODO Might be able to make an optimized version if we know a < c < b
    return Math.abs(c-a) < Math.abs(b-c);
  }

  public static double interpolate(double x, double y, double a) {
    return x + ((y - x) * a);
  }
  
    public static class SmallRandom {
      public long randState = System.nanoTime();
      
      public void setSeed(long seed) {
        randState = seed;
      }
    
      public long xorRand() {
        long x = randState;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        randState = x;
        return x >>> 1; // this might degrade randomness?  don't care too much
      }
    
      public double xorRandDbl() {
        long x = xorRand();
        return x / ((double)(Long.MAX_VALUE));
      }
      
      public int nextInt(int i) {
        return (int)(xorRand() % i);
      }
    }
    
    public static final double PLANCK_CONSTANT = 6.626070040e-34; // J*s
    public static final double SPEED_OF_LIGHT = 299792458; // m/s
    public static final double BOLTZMANN_CONSTANT = 1.38064852e-23; // J/K
    
    public static final double H = PLANCK_CONSTANT;
    public static final double C = SPEED_OF_LIGHT;
    public static final double K = BOLTZMANN_CONSTANT;
    
    public static final double _n = 1e-9; // nano
    public static final double _u = 1e-6; // micro
    public static final double _m = 1e-3; // milli
    public static final double _k = 1e+3; // kilo
    public static final double _M = 1e+6; // mega
    public static final double _G = 1e+9; // giga
    
    /**
     * Per Wikipedia: "I(ν,T) is the energy per unit time (or the power) radiated
     * per unit area of emitting surface in the normal direction per unit solid
     * angle per unit frequency by a black body at temperature T, also known as
     * spectral radiance."
     * Using wavelength instead of frequency.
     * @param wl in m
     * @param temp in K
     * @return 
     */
    public static double planckBlackBody(double wl, double temp) {
      return (((2 * H * C * C)/(wl * wl * wl * wl * wl)) / Math.expm1((H*C)/(wl*K*temp)));
      //return (((2 * H * freq * freq * freq)/(C * C)) / Math.expm1((H*freq)/(K*temp)));
    }
    
    /**
     * Maybe I'll do this later.  Integrate planckBlackBody * opsin absorption
     * over the range of visible wavelengths, ~350 nm to ~725 nm.
     * @param wl
     * @param temp
     * @return 
     */
    public static double[] planckRGB(double wl, double temp) {
      return new double[]{0, 0, 0};
    }
    
    /**
     * Returns x and y in the CIE chromaticity space.
     * @param temp
     * @return 
     */
    public static double[] planckianLocus(double temp) {
      double t = temp;
      double x, y;
      if (t < 1667) {
        x = 0.56463862848509;
        y = 0.40288701553742323;
      } else if (t <= 2222) {
        x = (-0.2661239e9 / (t * t * t)) + (-0.2343580e6 / (t * t)) + (0.8776956e3 / (t)) + (0.179910);
        y = (-1.1063814 * (x * x * x)) + (-1.34811020 * (x * x)) + (2.18555832 * (x)) + (-0.20219683);
        //x = 0.4;
        //y = 0.4;
      } else if (t <= 4000) {
        x = (-0.2661239e9 / (t * t * t)) + (-0.2343580e6 / (t * t)) + (0.8776956e3 / (t)) + (0.179910);
        y = (-0.9549476 * (x * x * x)) + (-1.37418593 * (x * x)) + (2.09137015 * (x)) + (-0.16748867);
      } else if (t <= 25000) {
        x = (-3.0258469e9 / (t * t * t)) + (2.1070379e6 / (t * t)) + (0.2226347e3 / (t)) + (0.240390);
        y = (3.0817580 * (x * x * x)) + (-5.87338670 * (x * x)) + (3.75112997 * (x)) + (-0.37001483);
      } else {
        x = 0.2524729944384;
        y = 0.2522547912436536;
      }
      return new double[]{x, y};
    }
    
    public static final double MAX_PLANCK_Y = 0.33735332946938534;  // The highest luminance I found for the planckian locus that didn't clip any red.
    
    /**
     * Assuming you're getting x and y from planckianLocus, might I recommend MAX_PLANCK_Y?
     * @param x
     * @param y
     * @param Y
     * @return 
     */
    public static double[] CIExyYtoXYZ(double x, double y, double Y) {
      return new double[]{(x*Y) / y, Y, (Y / y) * (1 - x - y)};
    }
    
    public static double[] CIEXYZtoRGB(double X, double Y, double Z, boolean bound) {
      double Rl = (3.2406*X) + (-1.5372*Y) + (-0.4986*Z);
      double Gl = (-0.9689*X) + (1.8758*Y) + (0.0415*Z);
      double Bl = (0.0557*X) + (-0.2040*Y) + (1.0570*Z);
      double a = 0.055;
      double R = ((Rl <= 0.0031308) ? (12.92 * Rl) : ((1 + a) * Math.pow(Rl, 1/2.4)) - a);
      double G = ((Gl <= 0.0031308) ? (12.92 * Gl) : ((1 + a) * Math.pow(Gl, 1/2.4)) - a);
      double B = ((Bl <= 0.0031308) ? (12.92 * Bl) : ((1 + a) * Math.pow(Bl, 1/2.4)) - a);
      if (bound) {
        R = bound(R, 0, 1);
        G = bound(G, 0, 1);
        B = bound(B, 0, 1);
      }
      return new double[]{R, G, B};
    }
    
    public static int RGBtoIntARGB(double R, double G, double B) {
      return (0xFF000000) + (((int)(0x00FF0000 * R)) & 0x00FF0000) + (((int)(0x0000FF00 * G)) & 0x0000FF00) + (((int)(0x000000FF * B)) & 0x000000FF);
    }

    
    /*
    The following two things came from http://www.fourmilab.ch/documents/specrend/
    */
    
  public static double CIE_COLOR_MATCH[][] = {
    {0.0014, 0.0000, 0.0065}, {0.0022, 0.0001, 0.0105}, {0.0042, 0.0001, 0.0201},
    {0.0076, 0.0002, 0.0362}, {0.0143, 0.0004, 0.0679}, {0.0232, 0.0006, 0.1102},
    {0.0435, 0.0012, 0.2074}, {0.0776, 0.0022, 0.3713}, {0.1344, 0.0040, 0.6456},
    {0.2148, 0.0073, 1.0391}, {0.2839, 0.0116, 1.3856}, {0.3285, 0.0168, 1.6230},
    {0.3483, 0.0230, 1.7471}, {0.3481, 0.0298, 1.7826}, {0.3362, 0.0380, 1.7721},
    {0.3187, 0.0480, 1.7441}, {0.2908, 0.0600, 1.6692}, {0.2511, 0.0739, 1.5281},
    {0.1954, 0.0910, 1.2876}, {0.1421, 0.1126, 1.0419}, {0.0956, 0.1390, 0.8130},
    {0.0580, 0.1693, 0.6162}, {0.0320, 0.2080, 0.4652}, {0.0147, 0.2586, 0.3533},
    {0.0049, 0.3230, 0.2720}, {0.0024, 0.4073, 0.2123}, {0.0093, 0.5030, 0.1582},
    {0.0291, 0.6082, 0.1117}, {0.0633, 0.7100, 0.0782}, {0.1096, 0.7932, 0.0573},
    {0.1655, 0.8620, 0.0422}, {0.2257, 0.9149, 0.0298}, {0.2904, 0.9540, 0.0203},
    {0.3597, 0.9803, 0.0134}, {0.4334, 0.9950, 0.0087}, {0.5121, 1.0000, 0.0057},
    {0.5945, 0.9950, 0.0039}, {0.6784, 0.9786, 0.0027}, {0.7621, 0.9520, 0.0021},
    {0.8425, 0.9154, 0.0018}, {0.9163, 0.8700, 0.0017}, {0.9786, 0.8163, 0.0014},
    {1.0263, 0.7570, 0.0011}, {1.0567, 0.6949, 0.0010}, {1.0622, 0.6310, 0.0008},
    {1.0456, 0.5668, 0.0006}, {1.0026, 0.5030, 0.0003}, {0.9384, 0.4412, 0.0002},
    {0.8544, 0.3810, 0.0002}, {0.7514, 0.3210, 0.0001}, {0.6424, 0.2650, 0.0000},
    {0.5419, 0.2170, 0.0000}, {0.4479, 0.1750, 0.0000}, {0.3608, 0.1382, 0.0000},
    {0.2835, 0.1070, 0.0000}, {0.2187, 0.0816, 0.0000}, {0.1649, 0.0610, 0.0000},
    {0.1212, 0.0446, 0.0000}, {0.0874, 0.0320, 0.0000}, {0.0636, 0.0232, 0.0000},
    {0.0468, 0.0170, 0.0000}, {0.0329, 0.0119, 0.0000}, {0.0227, 0.0082, 0.0000},
    {0.0158, 0.0057, 0.0000}, {0.0114, 0.0041, 0.0000}, {0.0081, 0.0029, 0.0000},
    {0.0058, 0.0021, 0.0000}, {0.0041, 0.0015, 0.0000}, {0.0029, 0.0010, 0.0000},
    {0.0020, 0.0007, 0.0000}, {0.0014, 0.0005, 0.0000}, {0.0010, 0.0004, 0.0000},
    {0.0007, 0.0002, 0.0000}, {0.0005, 0.0002, 0.0000}, {0.0003, 0.0001, 0.0000},
    {0.0002, 0.0001, 0.0000}, {0.0002, 0.0001, 0.0000}, {0.0001, 0.0000, 0.0000},
    {0.0001, 0.0000, 0.0000}, {0.0001, 0.0000, 0.0000}, {0.0000, 0.0000, 0.0000}
  };

  /**
   * Returns [x, y, z]
   */
  public double[] spectrum_to_xyz(RToRFunction specIntens) {
    int i;
    double lambda, X = 0, Y = 0, Z = 0, XYZ;

    for (i = 0, lambda = 380; lambda < 780.1; i++, lambda += 5) {
      double Me;

      Me = specIntens.evaluate(lambda);
      X += Me * CIE_COLOR_MATCH[i][0];
      Y += Me * CIE_COLOR_MATCH[i][1];
      Z += Me * CIE_COLOR_MATCH[i][2];
    }
    XYZ = (X + Y + Z);
    return new double[]{X / XYZ, Y / XYZ, Z / XYZ};
  }
  
}
