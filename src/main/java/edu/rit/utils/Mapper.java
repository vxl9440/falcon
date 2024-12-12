package edu.rit.utils;

import edu.rit.complex.LargeComplex;
import edu.rit.complex.SmallComplex;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Mapper {
    public static SmallComplex[] doubleToSmallComplex(double[] f) {
        SmallComplex[] cf = new SmallComplex[f.length];
        for (int i = 0; i < f.length; i++) {
            cf[i] = new SmallComplex(f[i], 0.0);
        }
        return cf;
    }

    public static double[] smallComplexToDouble(SmallComplex[] cf){
        double[] f = new double[cf.length];
        for (int i = 0; i < cf.length; i++) {
            f[i] = cf[i].r;
        }
        return f;
    }

    public static LargeComplex[] BigDecimalToLargeComplex(BigDecimal[] f) {
        LargeComplex[] cf = new LargeComplex[f.length];
        for (int i = 0; i < f.length; i++) {
            cf[i] = new LargeComplex(f[i], BigDecimal.ZERO);
        }
        return cf;
    }

    public static BigDecimal[] largeComplexToBigDecimal(LargeComplex[] f) {
        BigDecimal[] cf = new BigDecimal[f.length];
        for (int i = 0; i < f.length; i++) {
            cf[i] = f[i].r;
        }
        return cf;
    }

    public static double[] intToDouble(int[] f){
        double[] fn = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = f[i];
        }
        return fn;
    }

    public static BigInteger[] intToBigInt(int[] f){
        BigInteger[] fn = new BigInteger[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = BigInteger.valueOf(f[i]);
        }
        return fn;
    }

    public static int[] bigIntToInt(BigInteger[] f){
        int[] fn = new int[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = f[i].intValue();
        }
        return fn;
    }

    public static long[] bigIntToLong(BigInteger[] f){
        long[] fn = new long[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = f[i].longValue();
        }
        return fn;
    }

    public static BigInteger[] longToBigInt(long[] f){
        BigInteger[] fn = new BigInteger[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = BigInteger.valueOf(f[i]);
        }
        return fn;
    }

    public static BigInteger[] doubleBigInt(double[] f){
        BigInteger[] fn = new BigInteger[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = BigInteger.valueOf(Math.round(f[i]));
        }
        return fn;
    }

    public static int[] doubleToInt(double[] f){
        int[] fn = new int[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = (int) Math.round(f[i]);
        }
        return fn;
    }
}
