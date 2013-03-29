package mathnstuff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

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
}
