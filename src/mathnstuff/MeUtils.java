package mathnstuff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author erhannis
 */
public class MeUtils {
    public static final String COLOR_BLUE = ((char)0x1B) + "[34m";
    public static final String COLOR_RED = ((char)0x1B) + "[31m";
    public static final String COLOR_GREEN = ((char)0x1B) + "[32m";
    public static final String COLOR_RESET = ((char)0x1B) + "[0m";

    public static Object concatArrays(Object... arrays) {
        int length = 0;
        if (arrays.length == 0) {
            return null;
        }
        Class<?> c = null;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i] != null) {
                c = arrays[i].getClass();
                break;
            }
        }
        if (c == null || !c.isArray()) {
            return null;
        }
//        try {
        Class<?> ctype = c.getComponentType();

        if (ctype == byte.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((byte[])arrays[i]).length;
                }
            }
            byte[] result = new byte[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((byte[])arrays[i]).length);
                    index += ((byte[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == short.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((short[])arrays[i]).length;
                }
            }
            short[] result = new short[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((short[])arrays[i]).length);
                    index += ((short[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == int.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((int[])arrays[i]).length;
                }
            }
            int[] result = new int[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((int[])arrays[i]).length);
                    index += ((int[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == long.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((long[])arrays[i]).length;
                }
            }
            long[] result = new long[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((long[])arrays[i]).length);
                    index += ((long[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == float.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((float[])arrays[i]).length;
                }
            }
            float[] result = new float[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((float[])arrays[i]).length);
                    index += ((float[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == double.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((double[])arrays[i]).length;
                }
            }
            double[] result = new double[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((double[])arrays[i]).length);
                    index += ((double[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == boolean.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((boolean[])arrays[i]).length;
                }
            }
            boolean[] result = new boolean[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((boolean[])arrays[i]).length);
                    index += ((boolean[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == char.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((char[])arrays[i]).length;
                }
            }
            char[] result = new char[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((char[])arrays[i]).length);
                    index += ((char[])arrays[i]).length;
                }
            }
            return result;
        } else {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((Object[])arrays[i]).length;
                }
            }
            Object result = null;
            result = Array.newInstance(ctype, length); // AHA!  I HAVE FINALLY FOUND
                                                       //   VICTORY OVER JAVA AND ITS
                                                       //   GENERICS! - see http://stackoverflow.com/questions/529085/java-how-to-generic-array-creation
                                                       //   Well...most of a victory.
                                                       //   Still not total.  Grrr.
            //Object[] result = new Object[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((Object[])arrays[i]).length);
                    index += ((Object[])arrays[i]).length;
                }
            }
            return result;
        }
    }
    
    public static interface RecursionRunnable {
        public void run(int... dims);
    }
    
    public static void runRecursion(RecursionRunnable r, int level, int[] indices, int[] dims) {
        if (level < dims.length) {
            for (int i = 0; i < dims[level]; i++) {
                indices[level] = i;
                runRecursion(r, level + 1, indices, dims);
            }
        } else {
            r.run(indices);
        }
    }

    public static void runRecursion(RecursionRunnable r, int... dims) {
        int[] indices = new int[dims.length];
        runRecursion(r, 0, indices, dims);
    }
    
    public static Object randomDArray(final Random r, int... dimensions) {
        final Object result = Array.newInstance(double.class, dimensions);
        runRecursion(new RecursionRunnable() {
            public void run(int... index) {
                setInNArray(result, r.nextDouble(), index);
            }
        }, dimensions);
        return result;
    }
    
    public static Object getFromNArray(Object array, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        return bucket;
    }

    public static void setInNArrayObj(Object array, Object value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((Object[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, byte value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((byte[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, short value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((short[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, int value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((int[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, long value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((long[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, float value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((float[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, double value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((double[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, boolean value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((boolean[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, char value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((char[])bucket)[index[index.length - 1]] = value;
    }
    
    public static String arrayToString(Object array) { 
        StringBuilder sb = arrayToStringBuilder(array);
        return sb.toString();
    }

    public static StringBuilder arrayToStringBuilder(Object array) { 
        StringBuilder sb = new StringBuilder();
        if (array == null) {
            sb.append(array);
            return sb;
        }
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            sb.append("{");
            if (length > 0) {
                Object bucket = Array.get(array, 0);
                sb.append(arrayToStringBuilder(bucket));
            }
            for (int i = 1; i < length; i++) {
                Object bucket = Array.get(array, i);
                sb.append(", ");
                sb.append(arrayToStringBuilder(bucket));
            }
            sb.append("}");
        } else {
            sb.append(array.toString());
        }
        return sb;
    }
    
    /**
     * array must contain at least one object, and all the others should
     * be of the same type as the first.  It should also be of only one
     * dimension, at this point.
     * @param array
     * @return 
     */
    public static Object objectArrayToTArray(Object[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        Class<?> c = null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                c = array[i].getClass();
            }
        }
        Object result = java.lang.reflect.Array.newInstance(c, array.length);
        for (int i = 0; i < array.length; i++) {
            ((Object[])result)[i] = array[i];
        }
        return result;
    }
    
    public static String multiplyString(String s, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String multiplyString(String s, double num) {
        StringBuilder sb = new StringBuilder();
        int leftover = ((int)(s.length() * num)) % s.length();
        for (int i = 0; i < num; i++) {
            sb.append(s);
        }
        sb.append(s.substring(0, leftover));
        return sb.toString();
    }
    
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            char a = '0';
            switch (bytes[i] & 0xF0) {
                case 0x00:
                    a = '0';
                    break;
                case 0x10:
                    a = '1';
                    break;
                case 0x20:
                    a = '2';
                    break;
                case 0x30:
                    a = '3';
                    break;
                case 0x40:
                    a = '4';
                    break;
                case 0x50:
                    a = '5';
                    break;
                case 0x60:
                    a = '6';
                    break;
                case 0x70:
                    a = '7';
                    break;
                case 0x80:
                    a = '8';
                    break;
                case 0x90:
                    a = '9';
                    break;
                case 0xA0:
                    a = 'A';
                    break;
                case 0xB0:
                    a = 'B';
                    break;
                case 0xC0:
                    a = 'C';
                    break;
                case 0xD0:
                    a = 'D';
                    break;
                case 0xE0:
                    a = 'E';
                    break;
                case 0xF0:
                    a = 'F';
                    break;
            }
            char b = '0';
            switch (bytes[i] & 0x0F) {
                case 0x00:
                    b = '0';
                    break;
                case 0x01:
                    b = '1';
                    break;
                case 0x02:
                    b = '2';
                    break;
                case 0x03:
                    b = '3';
                    break;
                case 0x04:
                    b = '4';
                    break;
                case 0x05:
                    b = '5';
                    break;
                case 0x06:
                    b = '6';
                    break;
                case 0x07:
                    b = '7';
                    break;
                case 0x08:
                    b = '8';
                    break;
                case 0x09:
                    b = '9';
                    break;
                case 0x0A:
                    b = 'A';
                    break;
                case 0x0B:
                    b = 'B';
                    break;
                case 0x0C:
                    b = 'C';
                    break;
                case 0x0D:
                    b = 'D';
                    break;
                case 0x0E:
                    b = 'E';
                    break;
                case 0x0F:
                    b = 'F';
                    break;
            }
            sb.append(a);
            sb.append(b);
        }
        return sb.toString();
    }

    public static String rotateCCW(String s) {
        int maxLen = 0;
        int curLen = 0;
        int lines = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                curLen = 0;
                lines++;
            } else {
                curLen++;
                if (curLen > maxLen) {
                    maxLen = curLen;
                }
            }
        }
        char[][] chars = new char[maxLen][lines];
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < maxLen; x++) {
                chars[x][y] = ' ';
            }
        }
        int lineIndex = 0;
        int colIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                colIndex = 0;
                lineIndex++;
            } else {
                chars[colIndex][lineIndex] = s.charAt(i);
                colIndex++;                
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int x = maxLen - 1; x >= 0; x--) {
            for (int y = 0; y < lines; y++) {
                sb.append(chars[x][y]);
            }
            if (x > 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String rotateCW(String s) {
        int maxLen = 0;
        int curLen = 0;
        int lines = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                curLen = 0;
                lines++;
            } else {
                curLen++;
                if (curLen > maxLen) {
                    maxLen = curLen;
                }
            }
        }
        char[][] chars = new char[maxLen][lines];
        for (int y = 0; y < lines; y++) {
            for (int x = 0; x < maxLen; x++) {
                chars[x][y] = ' ';
            }
        }
        int lineIndex = 0;
        int colIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                colIndex = 0;
                lineIndex++;
            } else {
                chars[colIndex][lineIndex] = s.charAt(i);
                colIndex++;                
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < maxLen; x++) {
            for (int y = lines - 1; y >= 0; y--) {
                sb.append(chars[x][y]);
            }
            if (x < maxLen - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    public static Object getRandomElement(Collection c, Random r) {
        int i = r.nextInt(c.size());
        Iterator it = c.iterator();
        for (int j = 0; j < i; j++) {
            it.next();
        }
        return it.next();
    }
    
    public static int colorBytesToInt(byte r, byte g, byte b, byte a) {
        return (a * 0x01000000) + (r * 0x00010000) + (g * 0x00000100) + (b * 0x00000001);
    }
    
    public static void pipeInputStreamToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[256];
        int wrote = 0;
        while ((wrote = is.read(buf)) >= 0) {
            os.write(buf, 0, wrote);
        }
    }
    
    public static interface SearchKernel {
        public boolean beforeOrAt(long i);
        public long size();
    }
    
    /**
     * Find the first i satisfying beforeOrAt.
     * This has not been thoroughly proven or tested.
     * @param searchKernel
     * @return 
     */
    public static long binarySearch(SearchKernel searchKernel) {
        long size = searchKernel.size();
        if (size == 0) {
            return -1;
        }
        if (size == 1) {
            if (searchKernel.beforeOrAt(0)) {
                return 0;
            } else {
                return -1;
            }
        }
        long lstart = 0;
        long lend = (size / 2) - 1;
        long rstart = (size / 2);
        long rend = searchKernel.size() - 1;
        while (true) {
            if (searchKernel.beforeOrAt(lend)) {
                if (lstart == lend) {
                    return lend;
                }
                rend = lend;
                lend = ((rend - lstart + 1) / 2) + lstart - 1;
                rstart = lend + 1;
            } else {
                if (rstart == rend) {
                    if (searchKernel.beforeOrAt(rstart)) {
                        return rstart;
                    } else {
                        return -1;
                    }
                }
                lstart = rstart;
                lend = ((rend - lstart + 1) / 2) + lstart - 1;
                rstart = lend + 1;
            }
        }
    }
    
    public static long interpolateColors(long a, long b, double value) {
        long aa = (0xFF000000 & a) >> (8*3);
        long ar = (0x00FF0000 & a) >> (8*2);
        long ag = (0x0000FF00 & a) >> (8*1);
        long ab = (0x000000FF & a) >> (8*0);
        long ba = (0xFF000000 & b) >> (8*3);
        long br = (0x00FF0000 & b) >> (8*2);
        long bg = (0x0000FF00 & b) >> (8*1);
        long bb = (0x000000FF & b) >> (8*0);
        long ra = (long)((aa * (1 - value)) + (ba * value));
        long rr = (long)((ar * (1 - value)) + (br * value));
        long rg = (long)((ag * (1 - value)) + (bg * value));
        long rb = (long)((ab * (1 - value)) + (bb * value));
        return (ra * 0x01000000) + (rr * 0x00010000) + (rg * 0x00000100) + (rb * 0x00000001);
    }
}
