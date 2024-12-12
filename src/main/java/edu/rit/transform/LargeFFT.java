package edu.rit.transform;

import edu.rit.complex.LargeComplex;
import edu.rit.utils.Mapper;

import java.math.BigDecimal;

import static edu.rit.transform.root.RootFFT.rootsFFT2;
import static edu.rit.complex.LargeComplex.*;

public class LargeFFT{
    public static LargeComplex[] fft(BigDecimal[] f) {
        LargeComplex[] cf = Mapper.BigDecimalToLargeComplex(f);
        fft0(cf);
        return cf;
    }

    public static BigDecimal[] ifft(LargeComplex[] cf) {
        LargeComplex[] ccf = new LargeComplex[cf.length];
        System.arraycopy(cf, 0, ccf, 0, cf.length);
        ifft0(ccf);
        return Mapper.largeComplexToBigDecimal(ccf);
    }

    private static void fft0(LargeComplex[] cf) {
        int n = cf.length;
        if(n == 1) return;

        LargeComplex[] f0 = new LargeComplex[n >> 1];
        LargeComplex[] f1 = new LargeComplex[n >> 1];

        for(int i = 0;i < n;i+=2){
            f0[i >> 1] = cf[i];
            f1[i >> 1] = cf[i + 1];
        }

        fft0(f0);
        fft0(f1);

        for(int k = 0;k < (n >> 1);k++){
            LargeComplex t = multiply(rootsFFT2.get(n)[k], (f1[k]));
            cf[k] = add(f0[k], t);
            cf[k + (n >> 1)] = subtract(f0[k], t);
        }
    }

    private static void ifft0(LargeComplex[] cf) {
        int n = cf.length;
        if(n == 1) return;

        LargeComplex[] f0 = new LargeComplex[n >> 1];
        LargeComplex[] f1 = new LargeComplex[n >> 1];

        for(int k = 0;k < n / 2;k++){
            f0[k] = multiply(add(cf[k], cf[k + (n >> 1)]), HALF);
            f1[k] = multiply(multiply(HALF, rootsFFT2.get(n)[k]).conjugate(), subtract(cf[k], cf[k + (n >> 1)])) ;
        }

        ifft0(f0);
        ifft0(f1);

        for(int i = 0;i < n / 2;i++){
            cf[2 * i] = f0[i];
            cf[2 * i + 1] = f1[i];
        }
    }

}
