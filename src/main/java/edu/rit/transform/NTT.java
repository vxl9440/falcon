package edu.rit.transform;

import edu.rit.utils.Parameter;

import static edu.rit.transform.root.RootNTT.*;

public class NTT {

    public static int[] ntt(int[] f){
        int[] f1 = new int[f.length];
        System.arraycopy(f, 0, f1, 0, f1.length);
        ntt0(f1);
        return f1;
    }

    public static int[] intt(int[] f){
        int[] f1 = new int[f.length];
        System.arraycopy(f, 0, f1, 0, f1.length);
        intt0(f1);
        return f1;
    }

    private static void ntt0(int[] f){
        int n = f.length;
        if(n == 1) return;

        int[] f0 = new int[n >> 1];
        int[] f1 = new int[n >> 1];

        for(int i = 0;i < n;i+=2){
            f0[i >> 1] = f[i];
            f1[i >> 1] = f[i + 1];
        }

        ntt0(f0);
        ntt0(f1);

        for(int k = 0;k < (n >> 1);k++){
            f[k] = Math.floorMod(f0[k] + rootsNTT.get(n)[k] * f1[k], Parameter.q);
            f[k + (n >> 1)] = Math.floorMod(f0[k] - rootsNTT.get(n)[k] * f1[k], Parameter.q);
        }
    }

    private static void intt0(int[] f){
        int n = f.length;
        if(n == 1) return;

        int[] f0 = new int[n >> 1];
        int[] f1 = new int[n >> 1];

        for(int i = 0;i < n / 2;i++){
            f0[i] = Math.floorMod(inverse[2] * (f[i] + f[i + (n >> 1)]), Parameter.q);
            int t = Math.floorMod(inverse[2] * (f[i] - f[i + (n >> 1)]), Parameter.q);
            f1[i] = Math.floorMod(t * inverse[rootsNTT.get(n)[i]], Parameter.q);
        }

        intt0(f0);
        intt0(f1);

        for(int i = 0;i < n / 2;i++){
            f[i << 1] = f0[i];
            f[(i << 1) + 1] = f1[i];
        }
    }
}
