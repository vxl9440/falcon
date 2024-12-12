package edu.rit.sampler;

import edu.rit.complex.SmallComplex;
import edu.rit.entity.Basics;
import edu.rit.entity.FalconTree;
import edu.rit.entity.Pair;
import edu.rit.transform.SmallFFT;

import static edu.rit.polynomial.Operation.*;

public class FFTSampling {

    public static Pair<Basics> ldl(Basics G){
        SmallComplex[] D00 = G.e00;
        SmallComplex[] L10 = pfDivide(G.e10, G.e00);
        SmallComplex[] D11 = pfSubtract(G.e11, pfMultiply(L10, pfMultiply(pfAdjoint(L10), G.e00)));
        int n = G.e00.length;
        SmallComplex[] L00 = new SmallComplex[n];
        SmallComplex[] L01 = new SmallComplex[n];
        SmallComplex[] L11 = new SmallComplex[n];
        SmallComplex[] D01 = new SmallComplex[n];
        SmallComplex[] D10 = new SmallComplex[n];
        for(int i = 0;i < n;i++){
            L00[i] = SmallComplex.ONE;
            L01[i] = SmallComplex.ZERO;
            L11[i] = SmallComplex.ONE;
            D01[i] = SmallComplex.ZERO;
            D10[i] = SmallComplex.ZERO;
        }
        Basics L = new Basics(L00, L01, L10, L11);
        Basics D = new Basics(D00, D01, D10, D11);
        return new Pair<>(L, D);
    }

    public static FalconTree ffLDL(Basics G){
        Pair<Basics> ldl = ldl(G);
        Basics L = ldl.e1;
        Basics D = ldl.e2;
        FalconTree t = new FalconTree(L.e10);
        int n = G.e00.length;
        if(n == 2){
            t.left = new FalconTree(D.e00);
            t.right = new FalconTree(D.e11);
        }else{
            Pair<SmallComplex[]> pair00 = SmallFFT.splitFFT(D.e00);
            Pair<SmallComplex[]> pair11 = SmallFFT.splitFFT(D.e11);
            SmallComplex[] d00 = pair00.e1;
            SmallComplex[] d01 = pair00.e2;
            SmallComplex[] d10 = pair11.e1;
            SmallComplex[] d11 = pair11.e2;
            Basics G0 = new Basics(d00, d01, pfAdjoint(d01), d00);
            Basics G1 = new Basics(d10, d11, pfAdjoint(d11), d10);
            t.left = ffLDL(G0);
            t.right = ffLDL(G1);
        }
        return t;
    }

    public static void normalize(FalconTree t, double sig){
        if(t.left == null || t.right == null){
            t.value[0] = new SmallComplex(sig / Math.sqrt(t.value[0].r), 0.0);
            t.value[1] = SmallComplex.ZERO;
        }else{
            normalize(t.left, sig);
            normalize(t.right, sig);
        }
    }

    public static Pair<SmallComplex[]> ffSampling(Pair<SmallComplex[]> t, FalconTree ft, double sigMin){
        int n = t.e1.length;
        SmallComplex[] t0 = t.e1;
        SmallComplex[] t1 = t.e2;
        if(n == 1){
            double sigPrime = ft.value[0].r;
            int z0 = GaussianSampler.samplerZ(t0[0].r, sigMin ,sigPrime);
            int z1 = GaussianSampler.samplerZ(t1[0].r, sigMin ,sigPrime);
            return new Pair<>(new SmallComplex[]{new SmallComplex(z0, 0.0)},
                    new SmallComplex[]{new SmallComplex(z1, 0.0)});
        }else{
            Pair<SmallComplex[]> pt1 = SmallFFT.splitFFT(t1);
            Pair<SmallComplex[]> pz1 = ffSampling(pt1, ft.right, sigMin);
            SmallComplex[] z1 = SmallFFT.mergeFFT(pz1.e1, pz1.e2);
            SmallComplex[] t0Prime = pfAdd(t0, pfMultiply(pfSubtract(t1, z1), ft.value));
            Pair<SmallComplex[]> pt0 = SmallFFT.splitFFT(t0Prime);
            Pair<SmallComplex[]> pz0 = ffSampling(pt0, ft.left, sigMin);
            SmallComplex[] z0 = SmallFFT.mergeFFT(pz0.e1, pz0.e2);
            return new Pair<>(z0, z1);
        }
    }
}
