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
}
