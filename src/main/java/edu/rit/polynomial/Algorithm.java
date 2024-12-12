package edu.rit.polynomial;

import edu.rit.polynomial.convolve.LargeKaratsuba;
import edu.rit.utils.Parameter;

import java.math.BigInteger;
import java.util.stream.IntStream;

import static edu.rit.utils.Mapper.intToDouble;
import static edu.rit.polynomial.Operation.*;

public class Algorithm {

    public static double gramSchmidtNorm(int[] f, int[] g){
        double[] fd = intToDouble(f);
        double[] gd = intToDouble(g);
        double t1 = sqrtNorm(gd, fd);
        double[] af = polyAdjoint(fd);
        double[] ag = polyAdjoint(gd);
        double[] d = polyAdd(polyMultiply(fd, af), polyMultiply(gd, ag));
        double[] qf = polyScale(af, Parameter.q);
        double[] qg = polyScale(ag, Parameter.q);
        double t2 = sqrtNorm(polyDivide(qf, d), polyDivide(qg, d));
        return Math.max(t1, t2);
    }

    public static double sqrtNorm(double[] ... fs){
        double t = 0.0;
        for (double[] f : fs) {
            for (double c : f) {
                t += c * c;
            }
        }
        return t;
    }

    public static long sqrtNorm(int[] ... fs){
        long t = 0;
        for (int[] f : fs) {
            for (int c : f) {
                t += (long) c * c;
            }
        }
        return t;
    }

    public static BigInteger[] fieldNorm(BigInteger[] f){
        int n = f.length >> 1;
        BigInteger[] f0 = new BigInteger[n];
        BigInteger[] f1 = new BigInteger[n];
        for(int i = 0;i < n;i++){
            f0[i] = f[i << 1];
            f1[i] = f[(i << 1) + 1];
        }
        BigInteger[] f0Sqrt = LargeKaratsuba.polyMultiply(f0, f0);
        BigInteger[] f1Sqrt = LargeKaratsuba.polyMultiply(f1, f1);
//        BigInteger[] x = IntStream.range(0, n).mapToObj(i -> i == 1 ? BigInteger.ONE : BigInteger.ZERO).toArray(BigInteger[]::new);
//        return polySubtract(f0Sqrt, n != 1 ? LargeKaratsuba.polyMultiply(x, f1Sqrt) : polyNegate(f1Sqrt));
        f0Sqrt[0] = f0Sqrt[0].add(f1Sqrt[n - 1]);
        for(int i = 0;i < n - 1;i++){
            f0Sqrt[i + 1] = f0Sqrt[i + 1].subtract(f1Sqrt[i]);
        }
        return f0Sqrt;
    }

    // f(x^2)
    public static BigInteger[] xSqrtOfF(BigInteger[] f){
        BigInteger[] fn = new BigInteger[f.length << 1];
        for(int i = 0;i < fn.length;i++){
            fn[i] = i % 2 == 0 ? f[i >> 1] : BigInteger.ZERO;
        }
        return fn;
    }

    //f(-x)
    public static BigInteger[] negativeXofF(BigInteger[] f){
        BigInteger[] fn = new BigInteger[f.length];
        for(int i = 0;i < f.length;i++){
            fn[i] = i % 2 == 0 ? f[i] : f[i].negate();
        }
        return fn;
    }

    public static int bitSize(BigInteger n){
        n = n.abs();
        int bitLen = n.bitLength();
        int diff = n.bitLength() % 8;
        return bitLen + (diff == 0 ? 0 : 8 - diff);
    }

    public static int findHighestBit(BigInteger[] f, BigInteger[] g){
        BigInteger minF = f[0];
        BigInteger maxF = f[0];
        BigInteger minG = g[0];
        BigInteger maxG = g[0];

        for (int i = 1; i < f.length; i++) {
            if (minF.compareTo(f[i]) > 0) {
                minF = f[i];
            }
            if (maxF.compareTo(f[i]) < 0) {
                maxF = f[i];
            }
            if (minG.compareTo(g[i]) > 0) {
                minG = f[i];
            }
            if (maxG.compareTo(g[i]) < 0) {
                maxG = f[i];
            }
        }
        int highestBitF = Math.max(bitSize(minF), bitSize(maxF));
        int highestBitG = Math.max(bitSize(minG), bitSize(maxG));
        return Math.max(highestBitF, highestBitG);
    }
}
