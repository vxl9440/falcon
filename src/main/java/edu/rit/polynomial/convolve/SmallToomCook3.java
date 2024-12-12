package edu.rit.polynomial.convolve;

public class SmallToomCook3 {
    public static long[] tk3(long[] x, long[] y) {
        if(x.length <= 2 || y.length <= 2){
            return multiply(x, y);
        }

        long[][] xm = partition(x);
        long[] xm0 = xm[0];
        long[] xm1 = xm[1];
        long[] xm2 = xm[2];

        long[][] px = substitute(xm0, xm1, xm2);
        long[] p0  = px[0];
        long[] p1  = px[1];
        long[] pMinus1 = px[2];
        long[] pMinus2 = px[3];
        long[] pInf = px[4];

        long[][] ym = partition(y);
        long[] ym0 = ym[0];
        long[] ym1 = ym[1];
        long[] ym2 = ym[2];

        long[][] py = substitute(ym0, ym1, ym2);
        long[] q0  = py[0];
        long[] q1  = py[1];
        long[] qMinus1 = py[2];
        long[] qMinus2 = py[3];
        long[] qInf = py[4];

        long[] r0 = tk3(p0, q0);
        long[] r1 = tk3(p1, q1);
        long[] rMinus1 = tk3(pMinus1, qMinus1);
        long[] rMinus2 = tk3(pMinus2, qMinus2);
        long[] rInf = tk3(pInf, qInf);

        return interpolate(xm0.length, r0, r1, rMinus1, rMinus2, rInf);
    }

    // return p(0), p(1), p(-1), p(-2), p(inf)
    public static long[][] substitute(long[] m0, long[] m1, long[] m2) {
        long[][] p = new long[5][m0.length];
        for (int i = 0; i < m0.length; i++) {
            long p0 = m0[i] + m2[i];
            p[0][i] = m0[i];
            p[1][i] = p0 + m1[i];
            p[2][i] = p0 - m1[i];
            p[3][i] = (p[2][i] << 1) - m0[i] + (m2[i] << 1);
            p[4][i] = m2[i];
        }
        return p;
    }

    public static long[][] partition(long[] f) {
        int quotient = f.length / 3;
        int remainder = f.length % 3;
        int t = 3 - remainder;
        if(remainder > 0) quotient++;

        long[][] m = new long[3][quotient];
        for(int i = 0; i < 3; i++){
            if(remainder > 0 && i == 2){
                System.arraycopy(f, quotient * i, m[i], 0, quotient - t);
                for(int k = 0;k < t;k++){
                    m[i][quotient - t + k] = 0L;
                }
            }else{
                System.arraycopy(f, quotient * i, m[i], 0, quotient);
            }
        }
        return m;
    }


    public static long[] multiply(long[] x, long[] y) {
        int n = x.length;
        if(n == 1){
            return new long[]{x[0] * y[0], 0L};
        }else{
            return new long[]{x[0] * y[0],
                    x[1] * y[0] + x[0] * y[1],
                    x[1] * y[1], 0L};
        }
    }


    public static long[] interpolate(int n, long[] ... r) {
        if(r.length != 5) return null;
        long[][] result = new long[r.length][r[0].length];
        long[] r0 = r[0];
        long[] r1 = r[1];
        long[] rMinus1 = r[2];
        long[] rMinus2 = r[3];
        long[] rInf = r[4];
        for (int i = 0; i < r0.length; i++) {
            result[0][i] = r0[i];
            result[4][i] = rInf[i];
            result[3][i] = (rMinus2[i] - r1[i]) / 3L;
            result[1][i] = (r1[i] >> 1) - (rMinus1[i] >> 1);
            result[2][i] = rMinus1[i] - r0[i];
            result[3][i] = (result[2][i] >> 1) - (result[3][i] >> 1) + (rInf[i] << 1);
            result[2][i] = result[2][i] - result[4][i] + result[1][i];
            result[1][i] = result[1][i] - result[3][i];
        }

        long[] finalResult = lift(result[4], n * 4);

        for(int i = 3;i >= 0;i--){
            addArray(finalResult, result[i], n * i);
        }
        return finalResult;
    }

    public static long[] lift(long[] f, int n){
        long[] res = new long[f.length + n];
        for (int i = 0; i < n; i++) {
            res[i] = 0L;
        }
        System.arraycopy(f, 0, res, n, f.length);
        return res;
    }


    public static void addArray(long[] a, long[] b, int offset) {
        for(int i = 0;i < b.length;i++){
            a[i + offset] += b[i];
        }
    }

    
    public static long[] polyMultiply(long[] a, long[] b) {
        int n = a.length;
        long[] res = tk3(a, b);
        long[] finalResult = new long[n];
        for(int i = 0; i < n; i++){
            finalResult[i] = res[i] - res[i + n];
        }
        return finalResult;
    }
}
