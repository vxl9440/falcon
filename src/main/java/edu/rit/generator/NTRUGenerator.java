package edu.rit.generator;

import edu.rit.complex.SmallComplex;
import edu.rit.entity.NTRUPolynomials;
import edu.rit.exception.InvalidDegreeException;
import edu.rit.polynomial.convolve.LargeKaratsuba;
import edu.rit.polynomial.convolve.SmallKaratsuba;
import edu.rit.polynomial.convolve.SmallToomCook3;
import edu.rit.sampler.GaussianSampler;
import edu.rit.transform.NTT;
import edu.rit.transform.SmallFFT;
import edu.rit.utils.Common;
import edu.rit.utils.Mapper;
import edu.rit.utils.Parameter;

import java.math.BigInteger;

import static edu.rit.polynomial.Algorithm.*;
import static edu.rit.polynomial.Operation.*;
import static edu.rit.utils.Parameter.PRECISION;

public class NTRUGenerator {

    private final int n;
    private final double sigMin;

    public NTRUGenerator(int n, double sigMin) {
        if(!Common.isValidDegree(n)){
            throw new InvalidDegreeException("Invalid degree n");
        }
        this.n = n;
        this.sigMin = sigMin;
    }

    public NTRUPolynomials generate() {
        int[] f;
        int[] g = generatePolynomial(this.n, this.sigMin);

        while(true){
            f = generatePolynomial(this.n, this.sigMin);
            int[] fNTT = NTT.ntt(f);
            boolean flag = false;
            for (int i = 0; i < this.n; i++) {
                if(fNTT[i] == 0){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                break;
            }
        }

        double gamma = gramSchmidtNorm(f, g);
        if(gamma > 1.17 * 1.17 * Parameter.q){
            return generate(); // regenerate since f isn't invertible
        }
        BigInteger[] fi = Mapper.intToBigInt(f);
        BigInteger[] gi = Mapper.intToBigInt(g);
        try{
            BigInteger[][] solution = NTRUSolve(fi, gi);
            int[] F = Mapper.bigIntToInt(solution[0]);
            int[] G = Mapper.bigIntToInt(solution[1]);
            return new NTRUPolynomials(f, g, F, G);
        }catch (NumberFormatException nfe){
            return generate();
        }
    }

    public int[] generatePolynomial(int n, double sigMin){
//        double sigma = 1.17 * Math.sqrt(Parameter.q / 8192.0);
        double sigma = 1.43300980528773;
        int[] t = new int[4096];
        for(int i = 0;i < 4096;i++){
            t[i] = GaussianSampler.samplerZ(0, sigMin, sigma - 0.01);
        }

        int[] p = new int[n];
        int blockLen = 4096 / n;
        for(int i = 0;i < n;i++){
            int start = i * blockLen;
            int sum = 0;
            for(int j = 0;j < blockLen;j++){
                sum += t[start + j];
            }
            p[i] = sum;
        }
        return p;
    }

    private BigInteger[] exgcd(BigInteger f, BigInteger g){
        BigInteger r0 = f;
        BigInteger r1 = g;
        BigInteger s0 = BigInteger.ONE;
        BigInteger s1 = BigInteger.ZERO;
        BigInteger t0 = BigInteger.ZERO;
        BigInteger t1 = BigInteger.ONE;

        while(r1.compareTo(BigInteger.ZERO) != 0){
            BigInteger q = r0.divide(r1);
            BigInteger tempR = r1;
            r1 = r0.subtract(q.multiply(r1));
            r0 = tempR;
            BigInteger tempS = s1;
            s1 = s0.subtract(q.multiply(s1));
            s0 = tempS;
            BigInteger tempT = t1;
            t1 = t0.subtract(q.multiply(t1));
            t0 = tempT;
        }
        return new BigInteger[]{r0, s0, t0};
    }

    private BigInteger[][] NTRUSolve(BigInteger[] f, BigInteger[] g){
        int n = f.length;
        if(n == 1){
            BigInteger[] retVal = exgcd(f[0], g[0]);
            if(retVal[0].compareTo(BigInteger.ONE) != 0){
                throw new NumberFormatException();
            }
            BigInteger[] F = {retVal[2].multiply(BigInteger.valueOf(-Parameter.q))};
            BigInteger[] G = {retVal[1].multiply(BigInteger.valueOf(Parameter.q))};
            return new BigInteger[][] {F, G};
        }else{
            BigInteger[] fp = fieldNorm(f);
            BigInteger[] gp = fieldNorm(g);
            BigInteger[][] retVal = NTRUSolve(fp, gp);
            BigInteger[] Fp = retVal[0];
            BigInteger[] Gp = retVal[1];
            BigInteger[] F = LargeKaratsuba.polyMultiply(xSqrtOfF(Fp), negativeXofF(g));
            BigInteger[] G = LargeKaratsuba.polyMultiply(xSqrtOfF(Gp), negativeXofF(f));
            BigInteger[][] result = reduce(f, g, F, G);
            F = result[0];
            G = result[1];
            return new BigInteger[][] {F, G};
        }
    }

    private BigInteger[][] reduce(BigInteger[] f, BigInteger[] g, BigInteger[] F, BigInteger[] G) {
        int s1 = Math.max(53, findHighestBit(f, g));
        int n = f.length;
        double[] fShiftD = new double[n];
        double[] gShiftD = new double[n];
        for(int i = 0; i < f.length; i++){
            fShiftD[i] = f[i].shiftRight(s1 - PRECISION).doubleValue();
            gShiftD[i] = g[i].shiftRight(s1 - PRECISION).doubleValue();
        }
        long[] fd = null;
        long[] gd = null;
        if(n >= 256){
            fd = Mapper.bigIntToLong(f);
            gd = Mapper.bigIntToLong(g);
        }
        SmallComplex[] fFFT = SmallFFT.fft(fShiftD);
        SmallComplex[] gFFT = SmallFFT.fft(gShiftD);
        SmallComplex[] fAdjFFT = pfAdjoint(fFFT);
        SmallComplex[] gAdjFFT = pfAdjoint(gFFT);
        SmallComplex[] denominatorFFT = pfAdd(pfMultiply(fFFT, fAdjFFT), pfMultiply(gFFT, gAdjFFT));
        BigInteger[] k;
        while (true) {
            int s2 = Math.max(53, findHighestBit(F, G));
            if(s2 < s1) break;
            double[] FShiftD = new double[n];
            double[] GShiftD = new double[n];
            for(int i = 0; i < n; i++){
                FShiftD[i] = F[i].shiftRight(s2 - PRECISION).doubleValue();
                GShiftD[i] = G[i].shiftRight(s2 - PRECISION).doubleValue();
            }
            SmallComplex[] FFFT = SmallFFT.fft(FShiftD);
            SmallComplex[] GFFT = SmallFFT.fft(GShiftD);
            SmallComplex[] numerator = pfAdd(pfMultiply(FFFT, fAdjFFT), pfMultiply(GFFT, gAdjFFT));
            SmallComplex[] resultFFT = pfDivide(numerator, denominatorFFT);
            double[] result = SmallFFT.ifft(resultFFT);
            k = Mapper.doubleBigInt(result);

            boolean flag = false;
            for (int i = 0; i < n; i++) {
                if(k[i].compareTo(BigInteger.ZERO) != 0){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                break;
            }

            BigInteger[] t1;
            BigInteger[] t2;
            if(n >= 256){
                long[] kk = Mapper.bigIntToLong(k);
                if(n <= 512){
                    t1 = Mapper.longToBigInt(SmallKaratsuba.polyMultiply(kk, fd));
                    t2 = Mapper.longToBigInt(SmallKaratsuba.polyMultiply(kk, gd));
                }else{
                    t1 = Mapper.longToBigInt(SmallToomCook3.polyMultiply(kk, fd));
                    t2 = Mapper.longToBigInt(SmallToomCook3.polyMultiply(kk, gd));
                }
            }else{
                t1 = LargeKaratsuba.polyMultiply(k, f);
                t2 = LargeKaratsuba.polyMultiply(k, g);
            }

            for (int i = 0; i < n; i++) {
                F[i] = F[i].subtract(t1[i].shiftLeft(s2 - s1));
                G[i] = G[i].subtract(t2[i].shiftLeft(s2 - s1));
            }
        }
        return new BigInteger[][] {F, G};
    }
}
