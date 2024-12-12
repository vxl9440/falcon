package edu.rit.polynomial;

import edu.rit.complex.LargeComplex;
import edu.rit.complex.SmallComplex;
import edu.rit.transform.LargeFFT;
import edu.rit.transform.NTT;
import edu.rit.transform.SmallFFT;
import edu.rit.transform.root.RootNTT;
import edu.rit.utils.Parameter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Operation {
    
    // big integer
    public static BigInteger[] polyNegate(BigInteger[] f){
        BigInteger[] fn = new BigInteger[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = f[i].negate();
        }
        return fn;
    }

    public static BigInteger[] polyAdd(BigInteger[] f1, BigInteger[] f2){
        BigInteger[] fn = new BigInteger[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].add(f2[i]);
        }
        return fn;
    }

    public static BigInteger[] polySubtract(BigInteger[] f1, BigInteger[] f2){
        BigInteger[] fn = new BigInteger[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].subtract(f2[i]);
        }
        return fn;
    }
    
    
    // big decimal
    public static BigDecimal[] polyNegate(BigDecimal[] f){
        BigDecimal[] fn = new BigDecimal[f.length];
        for (int i = 0; i < f.length; i++) {
            fn[i] = f[i].negate();
        }
        return fn;
    }

    public static BigDecimal[] polyAdd(BigDecimal[] f1, BigDecimal[] f2){
        BigDecimal[] fn = new BigDecimal[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].add(f2[i]);
        }
        return fn;
    }

    public static BigDecimal[] polySubtract(BigDecimal[] f1, BigDecimal[] f2){
        BigDecimal[] fn = new BigDecimal[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].subtract(f2[i]);
        }
        return fn;
    }

    public static BigDecimal[] polyMultiply(BigDecimal[] f1, BigDecimal[] f2){
        return LargeFFT.ifft(lfMultiply(LargeFFT.fft(f1), LargeFFT.fft(f2)));
    }

    public static BigDecimal[] polyDivide(BigDecimal[] f1, BigDecimal[] f2){
        return LargeFFT.ifft(lfDivide(LargeFFT.fft(f1), LargeFFT.fft(f2)));
    }

    public static BigDecimal[] polyAdjoint(BigDecimal[] f1){
        return LargeFFT.ifft(lfAdjoint(LargeFFT.fft(f1)));
    }

    // double
    public static double[] polyNegate(double[] f){
        double[] fn = new double[f.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = -f[i];
        }
        return fn;
    }

    public static double[] polyAdd(double[] f1, double[] f2){
        double[] fn = new double[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i] + f2[i];
        }
        return fn;
    }

    public static double[] polySubtract(double[] f1, double[] f2){
        double[] fn = new double[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i] - f2[i];
        }
        return fn;
    }

    public static double[] polyMultiply(double[] f1, double[] f2){
        return SmallFFT.ifft(pfMultiply(SmallFFT.fft(f1), SmallFFT.fft(f2)));
    }

    public static double[] polyDivide(double[] f1, double[] f2){
        return SmallFFT.ifft(pfDivide(SmallFFT.fft(f1), SmallFFT.fft(f2)));
    }

    public static double[] polyAdjoint(double[] f1){
        return SmallFFT.ifft(pfAdjoint(SmallFFT.fft(f1)));
    }

    public static double[] polyScale(double[] f, double scale){
        double[] fn = new double[f.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f[i] * scale;
        }
        return fn;
    }

    // Large FFT
    public static LargeComplex[] lfAdd(LargeComplex[] f1, LargeComplex[] f2){
        LargeComplex[] fn = new LargeComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].add(f2[i]);
        }
        return fn;
    }

    public static LargeComplex[] lfSubtract(LargeComplex[] f1, LargeComplex[] f2){
        LargeComplex[] fn = new LargeComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].subtract(f2[i]);
        }
        return fn;
    }

    public static LargeComplex[] lfMultiply(LargeComplex[] f1, LargeComplex[] f2){
        LargeComplex[] fn = new LargeComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].multiply(f2[i]);
        }
        return fn;
    }

    public static LargeComplex[] lfDivide(LargeComplex[] f1, LargeComplex[] f2){
        LargeComplex[] fn = new LargeComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].divide(f2[i]);
        }
        return fn;
    }

    public static LargeComplex[] lfAdjoint(LargeComplex[] f){
        LargeComplex[] fn = new LargeComplex[f.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f[i].conjugate();
        }
        return fn;
    }

    // Primitive FFT
    public static SmallComplex[] pfAdd(SmallComplex[] f1, SmallComplex[] f2){
        SmallComplex[] fn = new SmallComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].add(f2[i]);
        }
        return fn;
    }

    public static SmallComplex[] pfSubtract(SmallComplex[] f1, SmallComplex[] f2){
        SmallComplex[] fn = new SmallComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].subtract(f2[i]);
        }
        return fn;
    }

    public static SmallComplex[] pfMultiply(SmallComplex[] f1, SmallComplex[] f2){
        SmallComplex[] fn = new SmallComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].multiply(f2[i]);
        }
        return fn;
    }

    public static SmallComplex[] pfDivide(SmallComplex[] f1, SmallComplex[] f2){
        SmallComplex[] fn = new SmallComplex[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f1[i].divide(f2[i]);
        }
        return fn;
    }

    public static SmallComplex[] pfScale(SmallComplex[] f, SmallComplex scale){
        SmallComplex[] fn = new SmallComplex[f.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f[i].multiply(scale);
        }
        return fn;
    }

    public static SmallComplex[] pfAdjoint(SmallComplex[] f){
        SmallComplex[] fn = new SmallComplex[f.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f[i].conjugate();
        }
        return fn;
    }

    public static double pfInnerProduct(SmallComplex[] f1, SmallComplex[] f2){
        double result = 0;
        for (int i = 0; i < f1.length; i++) {
            result += f1[i].multiply(f2[i].conjugate()).magnitude();
        }
        return result / f1.length;
    }

    public static SmallComplex[] pfNegate(SmallComplex[] f){
        SmallComplex[] fn = new SmallComplex[f.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = f[i].negate();
        }
        return fn;
    }

    // NTT for integer
    public static int[] polyAddQ(int[] f1, int[] f2){
        return nttAdd(f1, f2);
    }

    public static int[] polySubtractQ(int[] f1, int[] f2){
        return nttSubtract(f1, f2);
    }

    public static int[] polyMultiplyQ(int[] f1, int[] f2){
        return NTT.intt(nttMultiply(NTT.ntt(f1), NTT.ntt(f2)));
    }

    public static int[] polyDivideQ(int[] f1, int[] f2){
        return NTT.intt(nttDivideQ(NTT.ntt(f1), NTT.ntt(f2)));
    }

    // NTT operations
    public static int[] nttAdd(int[] f1, int[] f2){
        int[] fn = new int[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = Math.floorMod(f1[i] + f2[i], Parameter.q);
        }
        return fn;
    }

    public static int[] nttSubtract(int[] f1, int[] f2){
        int[] fn = new int[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = Math.floorMod(f1[i] - f2[i], Parameter.q);
        }
        return fn;
    }

    public static int[] nttMultiply(int[] f1, int[] f2){
        int[] fn = new int[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = Math.floorMod(f1[i] * f2[i], Parameter.q);
        }
        return fn;
    }

    public static int[] nttDivideQ(int[] f1, int[] f2){
        int[] fn = new int[f1.length];
        for (int i = 0; i < fn.length; i++) {
            fn[i] = Math.floorMod(f1[i] * RootNTT.inverse[f2[i]], Parameter.q);
        }
        return fn;
    }
}
