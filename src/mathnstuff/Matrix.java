package mathnstuff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A matrix class.  Important: it's indexed col then row, like vals[x][y].
 * Today I need to interact with things in y.x format.  This has led me to create
 * an abomination; a slew of "Tdnl" functions.  This means that the order of its
 * inputs and outputs is opposite from what it normally would be.  HOWEVER, the matrix
 * still internally stores its values x.y so it can work like normal - you just
 * have to be paranoid when getting and setting values, and converting it to other forms.
 * @author mewer12
 */
public class Matrix implements Streamable {

    public int cols = 0;
    public int rows = 0;
    public double[][] val = null;
    /** The binary digits after which two numbers are considered equal */
    public static int APRX = 24;
    public static CachedMatrix[][][] cache = null;
    public static final int CACHED_ROWS = 20;
    public static final int CACHED_COLS = 20;
    public static final int CACHED_COPIES = 20;
    public int usedCached = -1;

    static {
        cache = new CachedMatrix[CACHED_COLS][CACHED_ROWS][CACHED_COPIES];
        for (int i = 0; i < CACHED_COLS; i++) {
            for (int j = 0; j < CACHED_ROWS; j++) {
                for (int k = 0; k < CACHED_COPIES; k++) {
                    cache[i][j][k] = new CachedMatrix(i, j, k);
                }
            }
        }
    }

    public static class CachedMatrix {
        // Warning: Not thread safe yet

        public boolean inUse = false;
        public Matrix matrix = null;

        public CachedMatrix(int cols, int rows, int copy) {
            matrix = new Matrix(cols, rows);
            matrix.usedCached = copy;
        }
    }

    public Matrix(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.val = new double[cols][rows];
    }

    /**
     * Uggggh.  It's a super pain having to mix row.col vs col.row
     * @param rows
     * @param cols
     * @param value
     * @return 
     */
    public static Matrix digitMatrixTdnl(int rows, int cols, double value) {
      return digitMatrix(cols, rows, value);
    }

    public static Matrix zerosTdnl(int rows, int cols) {
      return digitMatrix(cols, rows, 0);
    }

    public static Matrix onesTdnl(int rows, int cols) {
      return digitMatrix(cols, rows, 1);
    }

    public static Matrix randTdnl(int rows, int cols, Random r) {
      return rand(cols, rows, r);
    }
    
    public static Matrix digitMatrix(int cols, int rows, double value) {
      Matrix result = new Matrix(cols, rows);
      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          result.val[x][y] = value;
        }
      }
      return result;
    }
    
    public static Matrix zeros(int cols, int rows) {
      return digitMatrix(cols, rows, 0);
    }

    public static Matrix ones(int cols, int rows) {
      return digitMatrix(cols, rows, 1);
    }
    
    public static Matrix rand(int cols, int rows, Random r) {
      Matrix result = new Matrix(cols, rows);
      for (int y = 0; y < rows; y++) {
        for (int x = 0; x < cols; x++) {
          result.val[x][y] = r.nextDouble();
        }
      }
      return result;
    }
    
    /**
     * Takes a list of lists of doubles IN y.x FORM, and returns a matrix
     * internally IN x.y FORM.
     * @return 
     */
    public static Matrix fromArrayListTdnl(ArrayList<ArrayList<Double>> array) {
      Matrix result = new Matrix(array.get(0).size(), array.size());
      for (int y = 0; y < result.rows; y++) {
        for (int x = 0; x < result.cols; x++) {
          result.val[x][y] = array.get(y).get(x);
        }
      }
      return result;
    }
    
    /**
     * Returns an list of lists of doubles IN y.x FORM.
     * @return 
     */
    public ArrayList<ArrayList<Double>> toArrayListTdnl() {
      ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
      for (int y = 0; y < rows; y++) {
        ArrayList<Double> row = new ArrayList<Double>();
        for (int x = 0; x < cols; x++) {
          row.add(val[x][y]);
        }
        result.add(row);
      }
      return result;
    }
//    /**
//     * Currently hard-coded to have up to 10x10 matrices in cache.  Will crash
//     * if you try to access one that isn't there.  If you want bigger ones cached,
//     * init the cache yourself like<br/>
//     * cache = new CachedMatrix[CACHED_COLS][CACHED_ROWS];<br/>
//     * for (int i = 0; i &lt; CACHED_COLS; i++) {<br/>
//     *     for (int j = 0; j &lt; CACHED_ROWS; j++) {<br/>
//     *         cache[i][j] = new CachedMatrix(i, j);<br/>
//     *     }<br/>
//     * }<br/>
//     * 
//     * @param cols
//     * @param rows
//     * @param init 
//     */
//    public Matrix(int cols, int rows, boolean init) {
//        this.cols = cols;
//        this.rows = rows;
//        for (int k = 0; k < CACHED_COPIES; k++) {
//            if (!cache[cols][rows][k].inUse) {
//                cache[cols][rows][k].inUse = true;
//                usedCached = k;
//                if (!init) {
//                    this.val = cache[cols][rows][k].val;
//                } else {
//                    this.val = cache[cols][rows][k].val;
//                    for (int i = 0; i < cols; i++) {
//                        for (int j = 0; j < rows; j++) {
//                            cache[cols][rows][k].val[i][j] = 0;
//                        }
//                    }
//                }
//                return;
//            }
//        }
//        if (usedCached == -1) {
//            this.val = new double[cols][rows];
//        }
//    }
    
    /**
     * Currently hard-coded to have up to 10x10 matrices in cache.  Will crash
     * if you try to access one that isn't there.  If you want bigger ones cached,
     * init the cache yourself like<br/>
     * cache = new CachedMatrix[CACHED_COLS][CACHED_ROWS][CACHED_COPIES];<br/>
     * for (int i = 0; i &lt; CACHED_COLS; i++) {<br/>
     *     for (int j = 0; j &lt; CACHED_ROWS; j++) {<br/>
     *         for (int k = 0; k &lt; CACHED_COPIES; k++) {<br/>
     *             cache[i][j][k] = new CachedMatrix(i, j, k);<br/>
     *         }<br/>
     *     }<br/>
     * }<br/>
     * 
     * @param cols
     * @param rows
     * @param init 
     */
    public static Matrix getCachedMatrix(int cols, int rows, boolean init) {
        for (int k = 0; k < CACHED_COPIES; k++) {
            if (!cache[cols][rows][k].inUse) {
                cache[cols][rows][k].inUse = true;
                cache[cols][rows][k].matrix.usedCached = k;
                if (!init) {
                    return cache[cols][rows][k].matrix;
                } else {
                    for (int i = 0; i < cols; i++) {
                        for (int j = 0; j < rows; j++) {
                            cache[cols][rows][k].matrix.val[i][j] = 0;
                        }
                    }
                    return cache[cols][rows][k].matrix;
                }
            }
        }
        return new Matrix(cols, rows);
    }

    public void doneWithMatrix() {
        if (usedCached != -1) {
            cache[cols][rows][usedCached].inUse = false;
        }
    }

    public Matrix(Matrix source) {
        this.cols = source.cols;
        this.rows = source.rows;
        this.val = new double[cols][rows];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                this.val[x][y] = source.val[x][y];
            }
        }
    }
    public boolean debug = false;

    /**
     * This matrix is an augmented matrix representing two lines which theoretically intersect.
     * Upon that assumption, it will try to get values for the two variables using some form of row-echelon reduction.
     * @return
     * @throws Exception 
     */
    public IntersectionResult ipSolveIntersect() throws Exception {
        //THINK I replaced == 1 with prettyEqual.  It might not be necessary, however.
        double[] qValues = {0, 0};
        boolean done = false;
        boolean qt1 = false;
        boolean qt2 = false;
        int t1 = 0;
        int t2 = 0;
        boolean qr1 = false;
        boolean qr2 = false;
        int r1 = 0;
        int r2 = 0;
        while (!done) {
            for (int y = 0; y < rows; y++) {
                if (MeMath.prettyZero(val[0][y], APRX)) {
                    if (MeMath.prettyZero(val[1][y], APRX)) {
                        // Hmm.  Pity.
                    } else if (!qt2 && MeMath.prettyEqual(val[1][y], 1, APRX)) {
                        qt2 = true;
                        t2 = y;
                        qValues[1] = val[2][y];
                    } else {
                        val[2][y] /= val[1][y];
                        val[1][y] = 1;//
                    }
                } else if (MeMath.prettyEqual(val[0][y], 1, APRX)) {
                    if (MeMath.prettyZero(val[1][y], APRX)) {
                        if (!qt1) {
                            if (!qr1) {
                                qr1 = true;
                                r1 = y;
                            }
                            qt1 = true;
                            t1 = y;
                            qValues[0] = val[2][y];
                        }
                    }/*else if (val[1][y] == 1) {
                    }*/ else {
                        if (qt1) {
                            if (qt2) {
                                // Oh, well, we're already done.
                            } else {
                                val[0][y] = 0;
                                val[1][y] -= val[1][t1];
                                val[2][y] -= val[2][t1];
                            }
                        } else {
                            if (!qr1) {
                                qr1 = true;
                                r1 = y;
                            }
                            if (qt2) {
                                val[0][y] -= (val[0][t2] * val[1][y]);
                                val[2][y] -= (val[2][t2] * val[1][y]);
                                val[1][y] = 0;
                            } else {
                                if (qr1 && r1 != y) {
                                    val[0][y] = 0;
                                    val[1][y] -= val[1][r1];
                                    val[2][y] -= val[2][r1];
                                }
                            }
                        }
                    }
                } else {
                    val[1][y] /= val[0][y];
                    val[2][y] /= val[0][y];
                    val[0][y] = 1;
                }
            }


            // Check done.
            for (int y = 0; y < rows; y++) {
                if (!qt1 && MeMath.prettyEqual(val[0][y], 1, APRX) && MeMath.prettyZero(val[1][y], APRX)) {
                    qt1 = true;
                    t1 = y;
                    qValues[0] = val[2][y];
                } else if (!qt2 && MeMath.prettyZero(val[0][y], APRX) && MeMath.prettyEqual(val[1][y], 1, APRX)) {
                    qt2 = true;
                    t2 = y;
                    qValues[1] = val[2][y];
                }
            }
            if ((qt1 && (!MeMath.prettyZero(qValues[0], APRX))) || (qt2 && (!MeMath.prettyZero(qValues[1], APRX)))) {
                done = true;
            }
            if (debug) {
                System.out.println(this);
            }
            if (qt1 && MeMath.prettyZero(qValues[0], APRX) && qt2 && MeMath.prettyZero(qValues[1], APRX)) {
                throw new Exception("Dead end!");
            }
        }
        if (qt1 && (!MeMath.prettyZero(qValues[0], APRX))) {
            return new IntersectionResult(1, qValues[0]);
        } else if (qt2 && (!MeMath.prettyZero(qValues[1], APRX))) {
            return new IntersectionResult(2, qValues[1]);
        } else {
            return new IntersectionResult(0, 0);
        }
//        return qValues;
    }

    public IntersectionResult solveIntersect() throws Exception {
        Matrix backup = new Matrix(this);
        IntersectionResult result = ipSolveIntersect();
        this.val = backup.val;
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < rows; y++) {
            if (y == 0) {
                result.append("/");
            } else if (y == (rows - 1)) {
                result.append("\\");
            } else {
                result.append("|");
            }

            for (int x = 0; x < cols; x++) {
                result.append(String.format("%7.4f", val[x][y]) + "\t");
            }
            if (y == 0) {
                result.append("\\\n");
            } else if (y == (rows - 1)) {
                result.append("/\n");
            } else {
                result.append("|\n");
            }
        }
        return result.toString();
    }
    public static CrossProducts cp = new CrossProducts();

    public static double[] cross(double[][] p) throws Exception {

        double[] result = new double[p.length + 1];
        boolean[] taken = new boolean[p.length + 1];
        for (int i = 0; i < p.length; i++) {
            if (p[i].length != (p.length + 1)) {
                throw new Exception("Must give n-1 vectors of n length!");
            }
        }
        for (int i = 0; i < (p.length + 1); i++) {
            taken[i] = false;
        }
        int[] state = {0};
        crossRecurse(result, 1, taken, 0, state, p.length + 1, p, new int[1], cp);
        return result;
    }

    private static void crossRecurse(double[] total, double product, boolean[] taken, int level, int[] state, int edgeLength, double[][] p, int[] curComponent, CrossProducts crossProductsA) {
        //prod take next available row, incmod state, if level == edgeLength +/- product, untake, return
        for (int y = -1; y < (edgeLength - 1); y++) {
            if (!taken[y + 1]) {
                double wasVal = product;
                if (y == -1) {
                    curComponent[0] = level;
                } else {
                    product *= p[y][level];
                }
                if (level == (edgeLength - 1)) {
                    /*                  switch (state[0]) {
                    case 0:
                    total[curComponent[0]] -= product * (1 - (2 * (edgeLength % 2)));
                    break;
                    case 1:
                    total[curComponent[0]] += product * (1 - (2 * (edgeLength % 2)));
                    break;
                    case 2:
                    total[curComponent[0]] += product * (1 - (2 * (edgeLength % 2)));
                    break;
                    case 3:
                    total[curComponent[0]] -= product * (1 - (2 * (edgeLength % 2)));
                    break;
                    }
                    state[0] = (state[0] + 1) % 4;
                     */
                    total[curComponent[0]] += product * crossProductsA.crossSign(edgeLength, state[0]);
//                    System.out.println(product * crossProductsA.sign(edgeLength, state[0]));
                    state[0]++;
                    break;
                } else {
                    taken[y + 1] = true;
                    crossRecurse(total, product, taken, level + 1, state, edgeLength, p, curComponent, crossProductsA);
                    taken[y + 1] = false;
                }
                product = wasVal;
            }
        }
//        return total.value;
    }

    public void ipRRowForm() { // Kinda.  It does its best to turn the left side into the identity matrix.
        if (debug) {
            System.out.println(this);
        }
        for (int row = 0; row < rows; row++) {
            if (row >= cols) {
                // Probably don't do anything for now.
            } else {
                if (MeMath.prettyZero(val[row][row], APRX)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < rows; y++) {
                        if (!MeMath.prettyZero(val[row][y], APRX)) {
                            for (int x = 0; x < cols; x++) {
                                val[x][row] += val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (debug) {
                        System.out.println(this);
                    }
                    if (!found) {
                        // Maybe I should error here.  We'll leave it, for now.
                        throw new RuntimeException("Matrix can't be left-identity.\n" + this.toString());
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(this);
//                        System.err.println();
                    }
                }
                for (int col = row + 1; col < cols; col++) {
                    val[col][row] /= val[row][row];
                }
                val[row][row] = 1;
                if (debug) {
                    System.out.println(this);
                }
                // Apply this row to all other ones.
                for (int y = 0; y < rows; y++) {
                    if (y != row) {
                        double factor = val[row][y];
                        for (int i = row; i < cols; i++) {
//                            System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                            val[i][y] -= factor * val[i][row];
                        }
                        if (debug) {
                            System.out.println(this);
                        }
                    }
                }
            }
        }

//        // Make identity.
//        int max = Math.min(rows, cols);
//        for (int row = 0; row < rows; row++) {
//            if (row >= cols) {
////                for (int x = 0; x < max; x++) {
////                    // Try to zero this spot in the row.
////                    for (int i = x; i < cols; i++) {
////                        val[i][row] -= val[x][row] * val[x][x];
////                    }
////if (debug) {
////    System.out.println(this);
////}
////                }
//            } else {
//                for (int x = row + 1; x < max; x++) {
//                    // Try to zero this spot in the row.
//                    for (int i = x; i < cols; i++) {
//                        val[i][row] -= val[x][row] * val[x][x];
//                    }
//if (debug) {
//    System.out.println(this);
//}
//                }
//            }
//        }
    }

    public void ipRRowFormMod() { // Kinda.  It does its best to turn the left side into the identity matrix.
        if (debug) {
            System.out.println(this);
        }
        for (int row = 0; (row < rows && row < cols - 1); row++) {
            if (row >= cols) {
                // Probably don't do anything for now.
            } else {
                if (MeMath.prettyZero(val[row][row], APRX)) {////
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < rows; y++) {
                        if (!MeMath.prettyZero(val[row][y], APRX)) {
                            for (int x = 0; x < cols; x++) {
                                val[x][row] += val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (debug) {
                        System.out.println(this);
                    }
                    if (!found) {
                        // Maybe I should error here.  We'll leave it, for now.
                        System.err.println("Matrix can't be left-identity.");
                        System.err.println(this);
                        System.err.println();
                    }
                }
                for (int col = row + 1; col < cols; col++) {
                    val[col][row] /= val[row][row];
                }
                val[row][row] = 1;
                if (debug) {
                    System.out.println(this);
                }
                // Apply this row to all other ones.
                for (int y = 0; y < rows; y++) {
                    if (y != row) {
                        double factor = val[row][y];
                        for (int i = row; i < cols; i++) {
//                            System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                            val[i][y] -= factor * val[i][row];
                        }
                        if (debug) {
                            System.out.println(this);
                        }
                    }
                }
            }
        }

//        // Make identity.
//        int max = Math.min(rows, cols);
//        for (int row = 0; row < rows; row++) {
//            if (row >= cols) {
////                for (int x = 0; x < max; x++) {
////                    // Try to zero this spot in the row.
////                    for (int i = x; i < cols; i++) {
////                        val[i][row] -= val[x][row] * val[x][x];
////                    }
////if (debug) {
////    System.out.println(this);
////}
////                }
//            } else {
//                for (int x = row + 1; x < max; x++) {
//                    // Try to zero this spot in the row.
//                    for (int i = x; i < cols; i++) {
//                        val[i][row] -= val[x][row] * val[x][x];
//                    }
//if (debug) {
//    System.out.println(this);
//}
//                }
//            }
//        }
    }

    public static Matrix lrJoin(Matrix l, Matrix r) throws Exception {
        if (l.rows != r.rows) {
            throw new Exception("Matrices are different heights!");
        }
        if (l.cols != r.cols) {
            System.err.println("Matrices are diff widths.  You'll have a hard time separating them.");
        }
        Matrix result = new Matrix(l.cols + r.cols, l.rows);
        for (int x = 0; x < l.cols; x++) {
            for (int y = 0; y < l.rows; y++) {
                result.val[x][y] = l.val[x][y];
            }
        }
        for (int x = 0; x < r.cols; x++) {
            for (int y = 0; y < r.rows; y++) {
                result.val[x + l.cols][y] = r.val[x][y];
            }
        }
        return result;
    }

    public static Matrix[] lrSplit(Matrix lr) throws Exception {
        if ((lr.cols % 2) != 0) {
            throw new Exception("Matrix doesn't have an even width!");
        }
        Matrix l = new Matrix(lr.cols / 2, lr.rows);
        Matrix r = new Matrix(lr.cols / 2, lr.rows);
        for (int x = 0; x < l.cols; x++) {
            for (int y = 0; y < l.rows; y++) {
                l.val[x][y] = lr.val[x][y];
            }
        }
        for (int x = 0; x < r.cols; x++) {
            for (int y = 0; y < r.rows; y++) {
                r.val[x][y] = lr.val[x + l.cols][y];
            }
        }
        Matrix[] result = {l, r};
        return result;
    }

    public static Matrix lrMult(Matrix l, Matrix r) throws Exception {
        if (l.cols != r.rows) {
            throw new Exception("Dims don't match for multiplication!");
        }
        Matrix result = new Matrix(r.cols, l.rows);
        for (int col = 0; col < result.cols; col++) {
            for (int row = 0; row < result.rows; row++) {
                double sum = 0;
                for (int i = 0; i < r.rows; i++) {
                    sum += l.val[i][row] * r.val[col][i];
                }
                result.val[col][row] = sum;
            }
        }
        return result;
    }

    public Matrix multM(Matrix m) {
        try {
            return Matrix.lrMult(this, m);
        } catch (Exception ex) {
            Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Matrix lrMultWCache(Matrix l, Matrix r) throws Exception {
        if (l.cols != r.rows) {
            throw new Exception("Dims don't match for multiplication!");
        }
        Matrix result = Matrix.getCachedMatrix(r.cols, l.rows, false);
        for (int col = 0; col < result.cols; col++) {
            for (int row = 0; row < result.rows; row++) {
                double sum = 0;
                for (int i = 0; i < r.rows; i++) {
                    sum += l.val[i][row] * r.val[col][i];
                }
                result.val[col][row] = sum;
            }
        }
        return result;
    }

    public static Matrix identity(int size) {
        Matrix m = new Matrix(size, size);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == y) {
                    m.val[x][y] = 1;
                } else {
                    m.val[x][y] = 0;
                }
            }
        }
        return m;
    }

    public static Matrix invert(Matrix m) throws Exception {
        if (m.cols != m.rows) {
            throw new Exception("Non-square matrix has no inverse! (More or less.)");
        }
        int size = m.cols;
        Matrix left = m;
        Matrix right = Matrix.identity(size);
        Matrix joined = Matrix.lrJoin(left, right);
        joined.ipRRowForm();
        Matrix[] split = Matrix.lrSplit(joined);
        return split[1];
    }
    
    public Matrix invert() throws Exception {
        return Matrix.invert(this);
    }

    public static NVector lrvMult(Matrix l, NVector r) throws Exception {
        if (l.cols != r.dims) {
            throw new Exception("Dims don't match for multiplication!");
        }
        NVector result = new NVector(l.rows);
        for (int row = 0; row < result.dims; row++) {
            double sum = 0;
            for (int i = 0; i < r.dims; i++) {
                sum += l.val[i][row] * r.coords[i];
            }
            result.coords[row] = sum;
        }
        return result;
    }
    
    public NVector multV(NVector v) {
        try {
            return Matrix.lrvMult(this, v);
        } catch (Exception ex) {
            Logger.getLogger(Matrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    

    /**
     * This attempts to figure out what the coordinates of each vector are
     * in the provided basis.  It was specifically designed to handle subspaces,
     * provided that all the vectors are in line with the basis.
     * The vectors returned will have dimension = number of bases.
     * I'm not actually sure why it's ip (in place).
     * @param bases
     * @param vectors
     * @return 
     */
    public static NVector[] ipTransformCoords(ArrayList<NVector> bases, ArrayList<NVector> vectors) {
        if (bases.isEmpty()) {
            NVector[] result = new NVector[vectors.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = new NVector(0);
            }
            return result;
        }
        // Set up the matrix
        //THINK This next matrix should MAYBE be initialized. - Nah, I think it does it all.
        Matrix m = Matrix.getCachedMatrix(bases.get(0).dims + bases.size(), bases.size() + vectors.size(), false);//MTXOFT*
        {
            int row = 0;
            for (int i = 0; i < bases.size(); i++, row++) {
                int col = 0;
                for (int j = 0; j < bases.get(i).dims; j++, col++) {
                    m.val[col][row] = bases.get(i).coords[j];
                }
                for (int j = 0; j < bases.size(); j++, col++) {
                    if (j == i) {
                        m.val[col][row] = 1;
                    } else {
                        m.val[col][row] = 0;
                    }
                }
            }
            for (int i = 0; i < vectors.size(); i++, row++) {
                int col = 0;
                for (int j = 0; j < vectors.get(i).dims; j++, col++) {
                    m.val[col][row] = vectors.get(i).coords[j];
                }
                for (int j = 0; j < bases.size(); j++, col++) {
                    m.val[col][row] = 0;
                }
            }
        }

        // Now solve it.
        int skipped = 0;
        for (int row = 0; row < bases.size(); row++) {
            if (row + skipped >= m.cols - 1) {
                // Probably don't do anything for now.
            } else {
                //if (m.val[row + skipped][row] == 0) {
                if (MeMath.prettyZero(m.val[row + skipped][row], APRX)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < bases.size(); y++) {
                        //if (m.val[row + skipped][y] != 0) {//m.val[2][y-1]
                        if (!MeMath.prettyZero(m.val[row + skipped][y], APRX)) {
                            for (int x = row + skipped; x < m.cols; x++) {
                                m.val[x][row] += m.val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //TODO This should check for...something.  Too many skips or something.  I don't care right now.
//                        if (row + skipped < ) I dunno.  Something should probably go here, but I'm going to leave it for now.
                        skipped++;
                        row--;
                        continue;
                        // Maybe I should error here.  We'll leave it, for now.
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(m);
//                        System.err.println();
                    }
                }
                // Div row by leading value
                for (int col = row + skipped + 1; col < m.cols; col++) {
                    m.val[col][row] /= m.val[row + skipped][row];
                }
                m.val[row + skipped][row] = 1;
                // Apply this row to all the following ones.
                for (int y = row + 1; y < m.rows; y++) {
                    double factor = m.val[row + skipped][y];
                    for (int i = row + skipped; i < m.cols; i++) {
//                        System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                        m.val[i][y] -= factor * m.val[i][row];
                    }
                }
            }
        }

        //System.out.println(m);

        // Extract the answer.
        NVector[] result = new NVector[vectors.size()];
        for (int i = 0; i < vectors.size(); i++) {
            NVector bucket = new NVector(bases.size());
            for (int j = 0; j < bucket.dims; j++) {
                bucket.coords[j] = -m.val[vectors.get(0).dims + j][bases.size() + i];
            }
            result[i] = bucket;
        }

        m.doneWithMatrix();
        return result;
    }

    /**
     * This attempts to figure out what the coordinates of each vector are
     * in the provided basis.  It was specifically designed to handle subspaces,
     * provided that all the vectors are in line with the basis.
     * The vectors returned will have dimension = number of bases.
     * Ugh, had to copy the whole friggin' thing just to use NVector[] instead of ArrayList<NVector>...
     * @param bases
     * @param vectors
     * @return 
     */
    public static NVector[] ipTransformCoords(NVector[] bases, NVector[] vectors) {
        if (bases.length == 0) {
            NVector[] result = new NVector[vectors.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = new NVector(0);
            }
            return result;
        }
        // Set up the matrix        
        Matrix m = new Matrix(bases[0].dims + bases.length, bases.length + vectors.length);
        {
            int row = 0;
            for (int i = 0; i < bases.length; i++, row++) {
                int col = 0;
                for (int j = 0; j < bases[i].dims; j++, col++) {
                    m.val[col][row] = bases[i].coords[j];
                }
                for (int j = 0; j < bases.length; j++, col++) {
                    if (j == i) {
                        m.val[col][row] = 1;
                    } else {
                        m.val[col][row] = 0;
                    }
                }
            }
            for (int i = 0; i < vectors.length; i++, row++) {
                int col = 0;
                for (int j = 0; j < vectors[i].dims; j++, col++) {
                    m.val[col][row] = vectors[i].coords[j];
                }
                for (int j = 0; j < bases.length; j++, col++) {
                    m.val[col][row] = 0;
                }
            }
        }

        // Now solve it.
        int skipped = 0;
        for (int row = 0; row < bases.length; row++) {
            if (row + skipped >= m.cols - 1) {
                // Probably don't do anything for now.
            } else {
                //if (m.val[row + skipped][row] == 0) {
                if (MeMath.prettyZero(m.val[row + skipped][row], APRX)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < bases.length; y++) {
                        //if (m.val[row + skipped][y] != 0) {
                        if (!MeMath.prettyZero(m.val[row + skipped][y], APRX)) {
                            for (int x = row + skipped; x < m.cols; x++) {
                                m.val[x][row] += m.val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //TODO This should check for...something.  Too many skips or something.  I don't care right now.
//                        if (row + skipped < ) I dunno.  Something should probably go here, but I'm going to leave it for now.
                        skipped++;
                        row--;
                        continue;
                        // Maybe I should error here.  We'll leave it, for now.
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(m);
//                        System.err.println();
                    }
                }
                for (int col = row + skipped + 1; col < m.cols; col++) {
                    m.val[col][row] /= m.val[row + skipped][row];
                }
                m.val[row + skipped][row] = 1;
                // Apply this row to all the following ones.
                for (int y = row + 1; y < m.rows; y++) {
                    double factor = m.val[row + skipped][y];
                    for (int i = row + skipped; i < m.cols; i++) {
//                        System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                        m.val[i][y] -= factor * m.val[i][row];
                    }
                }
            }
        }

        //System.out.println(m);

        // Extract the answer.
        NVector[] result = new NVector[vectors.length];
        for (int i = 0; i < vectors.length; i++) {
            NVector bucket = new NVector(bases.length);
            for (int j = 0; j < bucket.dims; j++) {
                bucket.coords[j] = -m.val[vectors[0].dims + j][bases.length + i];
            }
            result[i] = bucket;
        }

        return result;
    }

    /**
     * -EXPERIMENTAL-
     * Calculates the determinant of the matrix.
     * 
     * @return double
     */
    public double det() throws Exception {
        if (cols != rows) {
            throw new Exception("Can't find determinant for non-square matrix!");
        }
        DoubleHolder result = new DoubleHolder(0);
        boolean[] taken = new boolean[cols];
        for (int i = 0; i < cols; i++) {
            taken[i] = false;
        }
        int[] state = {0};
        detRecurse(result, 1, taken, 0, state);
        return result.value;
    }

    private class DoubleHolder {

        double value;

        public DoubleHolder(double value) {
            this.value = value;
        }
    }

    private class IntHolder {

        int value;

        public IntHolder(int value) {
            this.value = value;
        }
    }

    private void detRecurse(DoubleHolder total, double product, boolean[] taken, int level, int[] state) {
        //prod take next available row, incmod state, if level == edgeLength +/- product, untake, return
        for (int y = 0; y < cols; y++) {
            if (!taken[y]) {
                double wasVal = product;
                product *= this.val[level][y];
                if (level == (cols - 1)) {
                    /*                    switch (state.value) {
                    case 0:
                    total.value += product;
                    break;
                    case 1:
                    total.value -= product;
                    break;
                    case 2:
                    total.value -= product;
                    break;
                    case 3:
                    total.value += product;
                    break;
                    }
                    state.value = (state.value + 1) % 4;*/
                    total.value += product * this.cp.detSign(cols, state[0]);
                    state[0]++;
                    break;
                } else {
                    taken[y] = true;
                    this.detRecurse(total, product, taken, level + 1, state);
                    taken[y] = false;
                }
                product = wasVal;
            }
        }
//        return total.value;
    }

    public Matrix copy() {
        Matrix result = new Matrix(cols, rows);
        for (int x = 0; x < cols; x++) {
            System.arraycopy(this.val[x], 0, result.val[x], 0, rows);
//            for (int y = 0; y < rows; y++) {
//                result.val[x][y] = this.val[x][y];
//            }
        }
        return result;
    }

    public Matrix copyWCache() {
        Matrix result = Matrix.getCachedMatrix(cols, rows, false);
        for (int x = 0; x < cols; x++) {
            System.arraycopy(this.val[x], 0, result.val[x], 0, rows);
//            for (int y = 0; y < rows; y++) {
//                result.val[x][y] = this.val[x][y];
//            }
        }
        return result;
    }

    public Matrix transpose() {
        Matrix result = new Matrix(this.rows, this.cols);
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                result.val[j][i] = this.val[i][j];
            }
        }
        return result;
    }

    /**
     * Please note that this still allocates a new Matrix.
     * @return 
     */
    public Matrix transposeIP() {
        Matrix result = new Matrix(this.rows, this.cols);
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                result.val[j][i] = this.val[i][j];
            }
        }
        this.cols = result.cols;
        this.rows = result.rows;
        this.val = result.val;
        this.doneWithMatrix();
        this.usedCached = -1;
        return result;
    }

    public Matrix transposeWCache() {
        Matrix result = Matrix.getCachedMatrix(this.rows, this.cols, false);
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                result.val[j][i] = this.val[i][j];
            }
        }
        return result;
    }
    
    public static Matrix transpose(Matrix m) {
        Matrix result = new Matrix(m.rows, m.cols);
        for (int i = 0; i < m.cols; i++) {
            for (int j = 0; j < m.rows; j++) {
                result.val[j][i] = m.val[i][j];
            }
        }
        return result;
    }

    public static Matrix transposeWCache(Matrix m) {
        Matrix result = Matrix.getCachedMatrix(m.rows, m.cols, false);
        for (int i = 0; i < m.cols; i++) {
            for (int j = 0; j < m.rows; j++) {
                result.val[j][i] = m.val[i][j];
            }
        }
        return result;
    }

    public void ipRRowFormError() throws Exception { // Kinda.  It does its best to turn the left side into the identity matrix.
        if (debug) {
            System.out.println(this);
        }
        for (int row = 0; row < rows; row++) {
            if (row >= cols) {
                // Probably don't do anything for now.
            } else {
                if (MeMath.prettyZero(val[row][row], APRX)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < rows; y++) {
                        if (!MeMath.prettyZero(val[row][y], APRX)) {
                            for (int x = 0; x < cols; x++) {
                                val[x][row] += val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (debug) {
                        System.out.println(this);
                    }
                    if (!found) {
                        // Maybe I should error here.  We'll leave it, for now.
                        throw new Exception("Matrix can't be left-identity.\n" + this);
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(this);
//                        System.err.println();
                    }
                }
                for (int col = row + 1; col < cols; col++) {
                    val[col][row] /= val[row][row];
                }
                val[row][row] = 1;
                if (debug) {
                    System.out.println(this);
                }
                // Apply this row to all other ones.
                for (int y = 0; y < rows; y++) {
                    if (y != row) {
                        double factor = val[row][y];
                        for (int i = row; i < cols; i++) {
//                            System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                            val[i][y] -= factor * val[i][row];
                        }
                        if (debug) {
                            System.out.println(this);
                        }
                    }
                }
            }
        }
    }
    public int id = -2;

    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(cols);
        dos.writeInt(rows);
        dos.writeBoolean(debug);
        dos.writeInt(id);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                dos.writeDouble(val[i][j]);
            }
        }
    }

    public static Object fromBytes(DataInputStream dis) throws IOException {
        int cols = dis.readInt();
        int rows = dis.readInt();
        Matrix result = new Matrix(cols, rows);
        result.debug = dis.readBoolean();
        result.id = dis.readInt();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                result.val[i][j] = dis.readDouble();
            }
        }
        return result;
    }

    /**
     * This'll get an intersection of a line and an NPlane.  It works for fully determined systems,
     * but it also works for systems where technically the line might not quite intersect the nplane, actually.
     * However, they ought to be really really close, because it effectively just collapses a number of dimensions
     * equal to the extra - it reduces rows (Reduced Row Echelon format) until a solution is found, and then ignores the rest.
     * Good enough for now, anyway.
     * @param lPos
     * @param lDir
     * @param origin
     * @param bases
     * @return 
     */
    public static NVector lineNPlaneIntersect(NVector lPos, NVector lDir, NVector origin, NVector... bases) {
        Matrix solver = new Matrix(2 + bases.length, lPos.dims);
        for (int y = 0; y < solver.rows; y++) {
            solver.val[0][y] = lDir.coords[y];
        }
        for (int x = 1; x < bases.length + 1; x++) {
            for (int y = 0; y < solver.rows; y++) {
                solver.val[x][y] = -bases[x - 1].coords[y];
            }
        }
        int x = solver.cols - 1;
        for (int y = 0; y < solver.rows; y++) {//System.out.println(lPos.minusB(origin.plusB(bases[0])).length())
            solver.val[x][y] = origin.coords[y] - lPos.coords[y];
        }//System.out.println(solver);
        //TODO It might be a good idea to make this "closest points" for near-intersection.
        //TODO   Of course, this is probably faster, and maybe just as good for most things.
        //TODO   I'm already done with it, for sure.
        solver.ipRRowFormMod();
        double t = solver.val[solver.cols - 1][0];
        return lDir.multS(t).plusB(lPos);
    }

    /**
     * The two matrices are assumed to be equal dimensions, without checking.
     * @param m
     * @return 
     */
    public Matrix minusM(Matrix m) {
        Matrix result = new Matrix(cols, rows);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                result.val[x][y] = this.val[x][y] - m.val[x][y];
            }
        }
        return result;
    }
    
    /**
     * The two matrices are assumed to be equal dimensions, without checking.
     * @param m
     * @return 
     */
    public Matrix minusMWCache(Matrix m) {
        Matrix result = Matrix.getCachedMatrix(cols, rows, false);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                result.val[x][y] = this.val[x][y] - m.val[x][y];
            }
        }
        return result;
    }

    /**
     * The two matrices are assumed to be equal dimensions, without checking.
     * @param m
     * @return 
     */
    public Matrix minusMIP(Matrix m) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                this.val[x][y] -= m.val[x][y];
            }
        }
        return this;
    }

    /**
     * The two matrices are assumed to be equal dimensions, without checking.
     * @param m
     * @return 
     */
    public Matrix plusM(Matrix m) {
        Matrix result = new Matrix(cols, rows);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                result.val[x][y] = this.val[x][y] + m.val[x][y];
            }
        }
        return result;
    }
    
    /**
     * The two matrices are assumed to be equal dimensions, without checking.
     * @param m
     * @return 
     */
    public Matrix plusMWCache(Matrix m) {
        Matrix result = Matrix.getCachedMatrix(cols, rows, false);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                result.val[x][y] = this.val[x][y] + m.val[x][y];
            }
        }
        return result;
    }

    /**
     * The two matrices are assumed to be equal dimensions, without checking.
     * @param m
     * @return 
     */
    public Matrix plusMIP(Matrix m) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                this.val[x][y] += m.val[x][y];
            }
        }
        return this;
    }
    
    public Matrix multS(double s) {
        Matrix result = new Matrix(cols, rows);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                result.val[x][y] = this.val[x][y] * s;
            }
        }
        return result;
    }

    public Matrix multSWCache(double s) {
        Matrix result = Matrix.getCachedMatrix(cols, rows, false);
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                result.val[x][y] = this.val[x][y] * s;
            }
        }
        return result;
    }
    
    public Matrix multSIP(double s) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                this.val[x][y] *= s;
            }
        }
        return this;
    }
    
    @Override
    protected void finalize() throws Throwable {

        super.finalize();
    }
}