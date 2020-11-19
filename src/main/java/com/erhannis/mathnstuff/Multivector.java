/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

/**
 * Restricted to 3d, at least for now (and I may never get around to improving
 * it).
 *
 * This all is entirely thanks to this single page, whose information I sought
 * for 6 hours before stumbling across it here.
 * https://www.euclideanspace.com/maths/algebra/clifford/d3/arithmetic/index.htm
 * May blessings be heaped upon Martin John Baker's head.
 * <br/>
 * <br/>
 * So, quick rundown.  A multivector is like, an immiscible sum of a scalar,
 * a vector, a thing called a bivector, and a thing called a trivector.  (In
 * higher dimensions than three you can have more components.)  In fact, I THINK
 * that each of those things counts as a particular case of multivector - a
 * scalar is a multivector with all the non-scalar elements 0, or something.
 * You can do outer products, inner products, addition, multiplication,
 * technically division but I didn't implement that, and a thing called
 * geometric product, which is like the dot product plus the cross product
 * (inner plus outer products) and it has useful properties that let you do
 * rotations.  It's related to quaternions.<br/>
 * <br/>
 * Long story short, this is a heap of math that lets you rotate one vector X
 * by the rotation formed by going from vector A to B.
 * 
 * <br/><br/><br/>
 * Here's a bunch of test code I wrote that demonstrates its use:<br/>
 * <code><br/>
        System.out.println(Multivector.fromVector(1,0,0).innerIP(Multivector.fromVector(0,1,0)));<br/>
        System.out.println(Multivector.fromVector(1,0,0).outerIP(Multivector.fromVector(0,1,0)));<br/>
        Multivector a = Multivector.fromVector(1,0,0);<br/>
        Multivector b = Multivector.fromVector(0,1,0);<br/>
        Multivector c = Multivector.fromVector(1,1,1);<br/>
        Multivector x = a.add(b).divSIP(2);<br/>
        x = x.mulSIP(1/x.vectorNorm());<br/>
        System.out.println("a " + a);<br/>
        System.out.println("c " + c);<br/>
        System.out.println("a*c " + a.inner(c));<br/>
        <br/>
        System.out.println("");<br/>
        System.out.println("x     " + x);<br/>
        Multivector z = x.mul(a);<br/>
        System.out.println("xa    " + z);<br/>
        System.out.println("xac   " + z.mulIP(c));<br/>
        System.out.println("xaca  " + z.mulIP(a));<br/>
        System.out.println("xacax " + z.mulIP(x));<br/>
        System.out.println("");<br/>
<br/>
        System.out.println("xacax " + x.mul(a).mulIP(c).mulIP(a).mulIP(x));<br/>
        System.out.println("xacax " + Multivector.fromVector(Multivector.rotate(c.vectorComponent(), a.vectorComponent(), b.vectorComponent())));<br/>
   </code>
 * <br/>
 * One of the things I notice from this is that in working through the set of
 * geometric products from left to right, the leftmost item starts as a <br/>
 * <br/>
 * Also, seems like with unit vector `a`, and some vector `b`, `aba` is like,
 * an inversion of `b` through the line shot by `a`.  So like, if `a` is the
 * x-axis, the x-component of `b` will not change, but the other two will *-1.
 * <br/><br/>
 * Seems like with a unit bivector `a`, `aba` does similarly to a unit vector -
 * it reflects `b`, but through the plane I believe `a` describes.  So like,
 * e12 represents the xy plane, e31 represents the xz plane, and e23 represents
 * the yz plane.<br/>
 * Careful!  I got weird results when I tried to construct a bivector out of
 * non-perpendicular or non-unit vectors.  If I took the outer product of the
 * two vectors and "normalized" it (normalizing the bivector components the
 * same as I would the components of a normal vector - dunno if that's kosher),
 * then it gave the results I expected.
 * <br/><br/>
 * Finally, I think with the unit trivector `a`, `aba` inverts `b` through the
 * origin.
 * 
 * 
 * @author Erhannis
 */
public class Multivector {
    public double e;
    public double e1;
    public double e2;
    public double e3;
    public double e12;
    public double e31;
    public double e23;
    public double e123;

    public Multivector() {
    }
    
    public Multivector(double e, double e1, double e2, double e3, double e12, double e31, double e23, double e123) {
        this.e = e;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.e12 = e12;
        this.e31 = e31;
        this.e23 = e23;
        this.e123 = e123;
    }
    
    public static Multivector fromVector(double... xyz) {
        if (xyz.length != 3) {
            throw new IllegalArgumentException("must have 3 components");
        }
        return new Multivector(0, xyz[0], xyz[1], xyz[2], 0, 0, 0, 0);
    }
    
    public Multivector set(double e, double e1, double e2, double e3, double e12, double e31, double e23, double e123) {
        this.e = e;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.e12 = e12;
        this.e31 = e31;
        this.e23 = e23;
        this.e123 = e123;
        return this;
    }
    
    public Multivector clone() {
        Multivector c = new Multivector();
        c.set(e, e1, e2, e3, e12, e31, e23, e123);
        return c;
    }

    public Multivector mul(Multivector b) {
        Multivector a = this.clone();
        return a.mulIP(b);
    }

    public Multivector mulIP(Multivector b) {
        Multivector a = this;
        double e = a.e * b.e + a.e1 * b.e1 + a.e2 * b.e2 + a.e3 * b.e3 - a.e12 * b.e12 - a.e31 * b.e31 - a.e23 * b.e23 - a.e123 * b.e123;
        double e1 = a.e * b.e1 + a.e1 * b.e - a.e2 * b.e12 + a.e3 * b.e31 + a.e12 * b.e2 - a.e31 * b.e3 - a.e23 * b.e123 - a.e123 * b.e23;
        double e2 = a.e * b.e2 + a.e1 * b.e12 + a.e2 * b.e - a.e3 * b.e23 - a.e12 * b.e1 - a.e31 * b.e123 + a.e23 * b.e3 - a.e123 * b.e31;
        double e3 = a.e * b.e3 - a.e1 * b.e31 + a.e2 * b.e23 + a.e3 * b.e - a.e12 * b.e123 + a.e31 * b.e1 - a.e23 * b.e2 - a.e123 * b.e12;
        double e12 = a.e * b.e12 + a.e1 * b.e2 - a.e2 * b.e1 + a.e3 * b.e123 + a.e12 * b.e + a.e31 * b.e23 - a.e23 * b.e31 + a.e123 * b.e3;
        double e31 = a.e * b.e31 - a.e1 * b.e3 + a.e2 * b.e123 + a.e3 * b.e1 - a.e12 * b.e23 + a.e31 * b.e + a.e23 * b.e12 + a.e123 * b.e2;
        double e23 = a.e * b.e23 + a.e1 * b.e123 + a.e2 * b.e3 - a.e3 * b.e2 + a.e12 * b.e31 - a.e31 * b.e12 + a.e23 * b.e + a.e123 * b.e1;
        double e123 = a.e * b.e123 + a.e1 * b.e23 + a.e2 * b.e31 + a.e3 * b.e12 + a.e12 * b.e3 + a.e31 * b.e2 + a.e23 * b.e1 + a.e123 * b.e;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }

    public Multivector add(Multivector b) {
        Multivector a = this.clone();
        return a.addIP(b);
    }

    public Multivector addIP(Multivector b) {
        Multivector a = this;
        double e = a.e + b.e;
        double e1 = a.e1 + b.e1;
        double e2 = a.e2 + b.e2;
        double e3 = a.e3 + b.e3;
        double e12 = a.e12 + b.e12;
        double e31 = a.e31 + b.e31;
        double e23 = a.e23 + b.e23;
        double e123 = a.e123 + b.e123;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }

    public Multivector sub(Multivector b) {
        Multivector a = this.clone();
        return a.subIP(b);
    }

    public Multivector subIP(Multivector b) {
        Multivector a = this;
        double e = a.e - b.e;
        double e1 = a.e1 - b.e1;
        double e2 = a.e2 - b.e2;
        double e3 = a.e3 - b.e3;
        double e12 = a.e12 - b.e12;
        double e31 = a.e31 - b.e31;
        double e23 = a.e23 - b.e23;
        double e123 = a.e123 - b.e123;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }

    public Multivector mulS(double s) {
        Multivector a = this.clone();
        return a.mulSIP(s);
    }

    public Multivector mulSIP(double s) {
        Multivector a = this;
        double e = a.e * s;
        double e1 = a.e1 * s;
        double e2 = a.e2 * s;
        double e3 = a.e3 * s;
        double e12 = a.e12 * s;
        double e31 = a.e31 * s;
        double e23 = a.e23 * s;
        double e123 = a.e123 * s;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }

    public Multivector divS(double s) {
        Multivector a = this.clone();
        return a.divSIP(s);
    }

    public Multivector divSIP(double s) {
        Multivector a = this;
        double e = a.e / s;
        double e1 = a.e1 / s;
        double e2 = a.e2 / s;
        double e3 = a.e3 / s;
        double e12 = a.e12 / s;
        double e31 = a.e31 / s;
        double e23 = a.e23 / s;
        double e123 = a.e123 / s;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }

    public Multivector outer(Multivector b) {
        Multivector a = this.clone();
        return a.outerIP(b);
    }

    public Multivector outerIP(Multivector b) {
        Multivector a = this;
        double e = a.e * b.e;
        double e1 = a.e * b.e1 + a.e1 * b.e;
        double e2 = a.e * b.e2 + a.e2 * b.e;
        double e3 = a.e * b.e3 + a.e3 * b.e;
        double e12 = a.e * b.e12 + a.e1 * b.e2 - a.e2 * b.e1 + a.e12 * b.e;
        double e31 = a.e * b.e31 - a.e1 * b.e3 + a.e3 * b.e1 + a.e31 * b.e;
        double e23 = a.e * b.e23 + a.e2 * b.e3 - a.e3 * b.e2 + a.e23 * b.e;
        double e123 = a.e * b.e123 + a.e1 * b.e23 + a.e2 * b.e31 + a.e3 * b.e12 + a.e12 * b.e3 + a.e31 * b.e2 + a.e23 * b.e1 + a.e123 * b.e;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }

    public Multivector inner(Multivector b) {
        Multivector a = this.clone();
        return a.innerIP(b);
    }

    public Multivector innerIP(Multivector b) {
        Multivector a = this;
        double e = a.e1 * b.e1 + a.e2 * b.e2 + a.e3 * b.e3 - a.e12 * b.e12 - a.e31 * b.e31 - a.e23 * b.e23 - a.e123 * b.e123;
        double e1 = -a.e2 * b.e12 + a.e3 * b.e31 + a.e12 * b.e2 - a.e31 * b.e3 - a.e23 * b.e123 - a.e123 * b.e23;
        double e2 = a.e1 * b.e12 - a.e3 * b.e23 - a.e12 * b.e1 - a.e31 * b.e123 + a.e23 * b.e3 - a.e123 * b.e31;
        double e3 = -a.e1 * b.e31 + a.e2 * b.e23 - a.e12 * b.e123 + a.e31 * b.e1 - a.e23 * b.e2 - a.e123 * b.e12;
        double e12 = a.e3 * b.e123 + a.e123 * b.e3;
        double e31 = a.e2 * b.e123 + a.e123 * b.e2;
        double e23 = a.e1 * b.e123 + a.e123 * b.e1;
        double e123 = 0;
        return this.set(e, e1, e2, e3, e12, e31, e23, e123);
    }
    
    /**
     * Returns the norm of the vector portion of this thing
     * @return 
     */
    public double vectorNorm() {
        return Math.sqrt((e1*e1)+(e2*e2)+(e3*e3));
    }
    
    /**
     * Returns the vector portion of the multivector
     * @return 
     */
    public double[] vectorComponent() {
        return new double[]{e1,e2,e3};
    }

    /**
     * Returns the norm of the bivector portion of this thing.
     * Note that this is the L2 norm of the bivector components - I don't know
     * whether such a thing is even legal, but it seems to work for my
     * purposes.
     * @return 
     */
    public double bivectorNorm() {
        return Math.sqrt((e12*e12)+(e31*e31)+(e23*e23));
    }
    
    /**
     * Rotate vector x in the plane of a->b, by the angle separating a->b.
     * 
     * @param x
     * @param a
     * @param b
     * @return 
     */
    public static double[] rotate(double[] x, double[] a, double[] b) {
        Multivector x0 = Multivector.fromVector(x);
        Multivector a0 = Multivector.fromVector(a);
        Multivector b0 = Multivector.fromVector(b);
        a0.mulSIP(1/a0.vectorNorm());
        b0.addIP(a0).divSIP(2);
        b0.mulSIP(1/b0.vectorNorm());
        return b0.mul(a0).mulIP(x0).mulIP(a0).mulIP(b0).vectorComponent();
    }

    /**
     * Rotate multiple vectors x in the plane of a->b, by the angle separating a->b.
     * 
     * Basically a convenience method, saving a few Multivector operations.
     * 
     * @param x[COUNT][DIMS]
     * @param a
     * @param b
     * @return 
     * @see #rotate(double[], double[], double[]) 
     */
    public static double[][] rotate(double[][] x, double[] a, double[] b) {
        Multivector a0 = Multivector.fromVector(a);
        Multivector b0 = Multivector.fromVector(b);
        a0.mulSIP(1/a0.vectorNorm());
        b0.addIP(a0).divSIP(2);
        b0.mulSIP(1/b0.vectorNorm());
        Multivector BA = b0.mul(a0);
        Multivector AB = a0.mulIP(b0); // Kills a0
        double[][] results = new double[x.length][];
        for (int i = 0; i < x.length; i++) {
          Multivector x0 = Multivector.fromVector(x[i]);
          results[i] = BA.mul(x0).mulIP(AB).vectorComponent(); //TODO There's probably another corner to cut on allocations, here, but eh
        }
        return results;
    }

    /**
     * Rotate vector x in the plane of a->b, by the TWICE the angle separating a->b.
     * It's slightly more in-line with how the rotors want to be used.
     * May be useful when you're having trouble rotating 180*.
     * 
     * @param x
     * @param a
     * @param b
     * @return 
     */
    public static double[] rotate2(double[] x, double[] a, double[] b) {
        Multivector x0 = Multivector.fromVector(x);
        Multivector a0 = Multivector.fromVector(a);
        Multivector b0 = Multivector.fromVector(b);
        a0.mulSIP(1/a0.vectorNorm());
        b0.mulSIP(1/b0.vectorNorm());
        return b0.mul(a0).mulIP(x0).mulIP(a0).mulIP(b0).vectorComponent();
    }

    /**
     * Rotate multiple vectors x in the plane of a->b, by TWICE the angle separating a->b.
     * It's slightly more in-line with how the rotors want to be used.
     * May be useful when you're having trouble rotating 180*.
     * 
     * Basically a convenience method, saving a few Multivector operations.
     * 
     * @param x[COUNT][DIMS]
     * @param a
     * @param b
     * @return 
     * @see #rotate(double[], double[], double[]) 
     */
    public static double[][] rotate2(double[][] x, double[] a, double[] b) {
        Multivector a0 = Multivector.fromVector(a);
        a0.mulSIP(1/a0.vectorNorm());
        Multivector b0 = Multivector.fromVector(b);
        b0.mulSIP(1/b0.vectorNorm());
        Multivector BA = b0.mul(a0);
        Multivector AB = a0.mulIP(b0); // Kills a0
        double[][] results = new double[x.length][];
        for (int i = 0; i < x.length; i++) {
          Multivector x0 = Multivector.fromVector(x[i]);
          results[i] = BA.mul(x0).mulIP(AB).vectorComponent(); //TODO There's probably another corner to cut on allocations, here, but eh
        }
        return results;
    }
    
    /**
     * Mirror vector x across the plane of a->b.  This function is less 
     * trustworthy than the rotation functions, because it's based on me messing
     * with things, plugging in numbers, and going "well, it looks like that
     * does a reflection".  After writing this, I discovered a significant
     * oversight, and though I fixed that...you have been warned.
     * 
     * @param x
     * @param a
     * @param b
     * @return 
     */
    public static double[] mirror(double[] x, double[] a, double[] b) {
        Multivector x0 = Multivector.fromVector(x);
        Multivector r = Multivector.fromVector(a).outerIP(Multivector.fromVector(b));
        r.divSIP(r.bivectorNorm());
        return r.mul(x0).mulIP(r).vectorComponent();
    }

    /**
     * Mirror multiple vectors x across the plane of a->b.  Function is less
     * verified than rotation.
     * 
     * Basically a convenience method, saving a few Multivector operations.
     * 
     * @param x[COUNT][DIMS]
     * @param a
     * @param b
     * @return 
     * @see #mirror(double[], double[], double[]) 
     */
    public static double[][] mirror(double[][] x, double[] a, double[] b) {
        Multivector r = Multivector.fromVector(a).outerIP(Multivector.fromVector(b));
        r.divSIP(r.bivectorNorm());
        double[][] results = new double[x.length][];
        for (int i = 0; i < x.length; i++) {
          Multivector x0 = Multivector.fromVector(x[i]);
          results[i] = r.mul(x0).mulIP(r).vectorComponent();
        }
        return results;
    }

    /**
     * Mirror vector x through the line of a.  Equivalent to rotating 180* around a.
     * This function is less trustworthy than the rotation functions, because
     * it's based on me messing with things, plugging in numbers, and going
     * "well, it looks like that does a reflection".
     * 
     * @param x
     * @param a
     * @return 
     */
    public static double[] lineMirror(double[] x, double[] a) {
        Multivector x0 = Multivector.fromVector(x);
        Multivector a0 = Multivector.fromVector(MeMath.vectorNormalize(a));
        return a0.mul(x0).mulIP(a0).vectorComponent();
    }

    /**
     * Mirror multiple vectors x through the line of a.  Equivalent to rotating 180* around a.
     * This function is less trustworthy than the rotation functions, because
     * it's based on me messing with things, plugging in numbers, and going
     * "well, it looks like that does a reflection".
     * 
     * Basically a convenience method, saving a few Multivector operations.
     * 
     * @param x[COUNT][DIMS]
     * @param a
     * @return 
     * @see #lineMirror(double[], double[]) 
     */
    public static double[][] lineMirror(double[][] x, double[] a) {
        Multivector a0 = Multivector.fromVector(MeMath.vectorNormalize(a));
        double[][] results = new double[x.length][];
        for (int i = 0; i < x.length; i++) {
          Multivector x0 = Multivector.fromVector(x[i]);
          results[i] = a0.mul(x0).mulIP(a0).vectorComponent();
        }
        return results;
    }
    
    @Override
    public String toString() {
        return "{MV{" + e + "}{" + e1 + "," + e2 + "," + e3 + "}{" + e12 + "," + e31 + "," + e23 + "}{" + e123 + "}}";
    }
}

/* // More tests

      // Line reflection
      Multivector x = Multivector.fromVector(0,0,1);
      Multivector a = Multivector.fromVector(1.1,-0.3,0);
      Multivector b = Multivector.fromVector(1.1,-0.3,-2);
      Multivector c = Multivector.fromVector(9,6,1);
      Multivector d = Multivector.fromVector(-1,-2,-3);

//      System.out.println("a   " + a);
//      System.out.println("xa " + x.mul(a));
//      System.out.println("ax " + a.mul(x));
//      System.out.println("xax " + x.mul(a).mul(x));

      System.out.println("a   " + a);
      System.out.println("xax " + x.mul(a).mul(x));
      System.out.println("b   " + b);
      System.out.println("xbx " + x.mul(b).mul(x));
      System.out.println("c   " + c);
      System.out.println("xcx " + x.mul(c).mul(x));
      System.out.println("a   " + d);
      System.out.println("xdx " + x.mul(d).mul(x));
      System.out.println("");
      System.out.println("a-xax " + a.sub(x.mul(a).mul(x)));
      System.out.println("b-xbx " + b.sub(x.mul(b).mul(x)));
      System.out.println("c-xcx " + c.sub(x.mul(c).mul(x)));
      System.out.println("d-xdx " + d.sub(x.mul(d).mul(x)));




      // Plane reflection ; use above block
      Multivector x = new Multivector(0,0,0,0,0,1,0,0);

      // Also plane reflection
      Multivector y = Multivector.fromVector(1,0,1).divSIP(Math.sqrt(2));
      Multivector z = Multivector.fromVector(0,-1,0);
      Multivector x = z.mul(y);

      System.out.println("y   " + y);
      System.out.println("z   " + z);

      // Also plane reflection
      Multivector a = Multivector.fromVector(1,-3.5,0);
      Multivector b = Multivector.fromVector(-2,0.5,0);
      Multivector c = a.outer(b);
      c.divSIP(c.bivectorNorm());


*/