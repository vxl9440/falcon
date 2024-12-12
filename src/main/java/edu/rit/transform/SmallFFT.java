package edu.rit.transform;

import edu.rit.complex.SmallComplex;
import edu.rit.entity.Pair;
import edu.rit.utils.Mapper;

import static edu.rit.complex.SmallComplex.*;
import static edu.rit.transform.root.RootFFT.rootsFFT1;

public class SmallFFT {
    public static SmallComplex[] fft(double[] f) {
        SmallComplex[] cf = Mapper.doubleToSmallComplex(f);
        fft0(cf);
        return cf;
    }

    public static double[] ifft(SmallComplex[] cf) {
        SmallComplex[] ccf = new SmallComplex[cf.length];
        System.arraycopy(cf, 0, ccf, 0, cf.length);
        ifft0(ccf);
        return Mapper.smallComplexToDouble(ccf);
    }

    public static void fft0(SmallComplex[] cf) {
        int n = cf.length;
        if(n == 1) return;

        SmallComplex[] f0 = new SmallComplex[n >> 1];
        SmallComplex[] f1 = new SmallComplex[n >> 1];

        for(int i = 0;i < n;i+=2){
            f0[i >> 1] = cf[i];
            f1[i >> 1] = cf[i + 1];
        }

        fft0(f0);
        fft0(f1);

        for(int k = 0;k < (n >> 1);k++){
            SmallComplex t = multiply(rootsFFT1.get(n)[k] , f1[k]);
            cf[k] = add(f0[k], t);
            cf[k + (n >> 1)] = subtract(f0[k], t);
        }
    }

    public static void ifft0(SmallComplex[] cf) {
        int n = cf.length;
        if(n == 1) return;

        Pair<SmallComplex[]> p = splitFFT(cf);
        SmallComplex[] f0 = p.e1;
        SmallComplex[] f1 = p.e2;

        ifft0(f0);
        ifft0(f1);

        for(int i = 0;i < n >> 1;i++){
            cf[i << 1] = f0[i];
            cf[(i << 1) + 1] = f1[i];
        }
    }

    public static Pair<SmallComplex[]> splitFFT(SmallComplex[] cf) {
        int n = cf.length;
        SmallComplex[] f0 = new SmallComplex[n >> 1];
        SmallComplex[] f1 = new SmallComplex[n >> 1];
        for(int k = 0;k < (n >> 1);k++){
            f0[k] = multiply(add(cf[k], cf[k + (n >> 1)]), HALF);
            f1[k] = multiply(subtract(cf[k], cf[k + (n >> 1)]), multiply(HALF, rootsFFT1.get(n)[k]).conjugate()) ;
        }
        return new Pair<>(f0, f1);
    }

    public static SmallComplex[] mergeFFT(SmallComplex[] f0, SmallComplex[] f1) {
        int n = f0.length << 1;
        SmallComplex[] cf = new SmallComplex[n];
        for(int k = 0;k < n >> 1;k++){
            SmallComplex t = multiply(rootsFFT1.get(n)[k] , f1[k]);
            cf[k] = add(f0[k], t);
            cf[k + (n >> 1)] = subtract(f0[k], t);
        }
        return cf;
    }
}
