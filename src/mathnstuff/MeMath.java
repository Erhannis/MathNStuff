package mathnstuff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import networks.Edge;
import networks.Network;
import networks.Node;

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
    
    /**
     * Really only accurate within [-2,2], at n=20.
     * @param x
     * @param n
     * @return 
     */
    public static final double erf(double x, int n) {
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
    public static final double erf(double x) {
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
    public static void normalizeVectorIP(double[] u) {
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
}
