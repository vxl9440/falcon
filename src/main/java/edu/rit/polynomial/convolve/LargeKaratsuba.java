package edu.rit.polynomial.convolve;

import java.math.BigInteger;
import java.util.Arrays;

public class LargeKaratsuba {
    private static BigInteger[] karatsuba(BigInteger[] x, BigInteger[] y) {
        int n = x.length;
        if (n == 1) {
            return new BigInteger[] { x[0].multiply(y[0]), BigInteger.ZERO };
        }

        int half = n / 2;

        BigInteger[] xLow = Arrays.copyOfRange(x, 0, half);
        BigInteger[] xHigh = Arrays.copyOfRange(x, half, n);
        BigInteger[] yLow = Arrays.copyOfRange(y, 0, half);
        BigInteger[] yHigh = Arrays.copyOfRange(y, half, n);

        BigInteger[] z0 = karatsuba(xLow, yLow);
        BigInteger[] z2 = karatsuba(xHigh, yHigh);

        BigInteger[] xLowHighSum = addArrays(xLow, xHigh);
        BigInteger[] yLowHighSum = addArrays(yLow, yHigh);

        BigInteger[] z1 = karatsuba(xLowHighSum, yLowHighSum);
        z1 = subtractArrays(z1, z0);
        z1 = subtractArrays(z1, z2);

        BigInteger[] result = new BigInteger[2 * n];
        Arrays.fill(result, BigInteger.ZERO);

        addToResult(result, z0, 0);
        addToResult(result, z1, half);
        addToResult(result, z2, 2 * half);

        return result;
    }

    private static BigInteger[] addArrays(BigInteger[] a, BigInteger[] b) {
        int maxLength = Math.max(a.length, b.length);
        BigInteger[] result = new BigInteger[maxLength];

        for (int i = 0; i < maxLength; i++) {
            BigInteger aValue = (i < a.length) ? a[i] : BigInteger.ZERO;
            BigInteger bValue = (i < b.length) ? b[i] : BigInteger.ZERO;
            result[i] = aValue.add(bValue);
        }

        return result;
    }

    private static BigInteger[] subtractArrays(BigInteger[] a, BigInteger[] b) {
        int maxLength = Math.max(a.length, b.length);
        BigInteger[] result = new BigInteger[maxLength];
        for (int i = 0; i < maxLength; i++) {
            BigInteger aValue = (i < a.length) ? a[i] : BigInteger.ZERO;
            BigInteger bValue = (i < b.length) ? b[i] : BigInteger.ZERO;
            result[i] = aValue.subtract(bValue);
        }

        return result;
    }

    private static void addToResult(BigInteger[] result, BigInteger[] toAdd, int offset) {
        for (int i = 0; i < toAdd.length; i++) {
            result[i + offset] = result[i + offset].add(toAdd[i]);
        }
    }

    public static BigInteger[] polyMultiply(BigInteger[] a, BigInteger[] b) {
        int n = a.length;
        BigInteger[] res = karatsuba(a, b);
        BigInteger[] finalResult = new BigInteger[n];
        for(int i = 0; i < n; i++){
            finalResult[i] = res[i].subtract(res[i + n]);
        }
        return finalResult;
    }
}
