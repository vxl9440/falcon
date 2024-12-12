package edu.rit.polynomial.convolve;

import java.util.Arrays;

public class SmallKaratsuba {
    private static long[] karatsuba(long[] x, long[] y) {
        int n = x.length;
        if (n == 1) {
            return new long[] { x[0] * y[0], 0L };
        }

        int half = n >> 1;

        long[] xLow = Arrays.copyOfRange(x, 0, half);
        long[] xHigh = Arrays.copyOfRange(x, half, n);
        long[] yLow = Arrays.copyOfRange(y, 0, half);
        long[] yHigh = Arrays.copyOfRange(y, half, n);

        long[] z0 = karatsuba(xLow, yLow);
        long[] z2 = karatsuba(xHigh, yHigh);

        long[] xLowHighSum = addArrays(xLow, xHigh);
        long[] yLowHighSum = addArrays(yLow, yHigh);

        long[] z1 = karatsuba(xLowHighSum, yLowHighSum);
        z1 = subtractArrays(z1, z0);
        z1 = subtractArrays(z1, z2);

        long[] result = new long[n << 1];
        Arrays.fill(result, 0L);

        addToResult(result, z0, 0);
        addToResult(result, z1, half);
        addToResult(result, z2, half << 1);

        return result;
    }

    // Helper method to add two arrays element-wise
    private static long[] addArrays(long[] a, long[] b) {
        int maxLength = Math.max(a.length, b.length);
        long[] result = new long[maxLength];

        for (int i = 0; i < maxLength; i++) {
            long aValue = (i < a.length) ? a[i] : 0L;
            long bValue = (i < b.length) ? b[i] : 0L;
            result[i] = aValue + bValue;
        }

        return result;
    }

    // Helper method to subtract two arrays element-wise
    private static long[] subtractArrays(long[] a, long[] b) {
        int maxLength = Math.max(a.length, b.length);
        long[] result = new long[maxLength];
        for (int i = 0; i < maxLength; i++) {
            long aValue = (i < a.length) ? a[i] : 0L;
            long bValue = (i < b.length) ? b[i] : 0L;
            result[i] = aValue - bValue;
        }

        return result;
    }

    private static void addToResult(long[] result, long[] toAdd, int offset) {
        for (int i = 0; i < toAdd.length; i++) {
            result[i + offset] += toAdd[i];
        }
    }


    public static long[] polyMultiply(long[] a, long[] b) {
        int n = a.length;
        long[] res = karatsuba(a, b);
        long[] finalResult = new long[n];
        for(int i = 0; i < n; i++){
            finalResult[i] = res[i] - res[i + n];
        }
        return finalResult;
    }
}
