package edu.rit.sampler;


import edu.rit.utils.Parameter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class GaussianSampler {
    public static final Random secureRandom = new SecureRandom();
    private static final double SIG_MAX_2_SQRT = 2 * Parameter.SIG_MAX * Parameter.SIG_MAX;  // 2 * SIG_MAX^2
    private static final double LN2 = 0.693147180559;


    private static final BigInteger[] RCDT = {
            new BigInteger("3024686241123004913666"),
            new BigInteger("1564742784480091954050"),
            new BigInteger("636254429462080897535"),
            new BigInteger("199560484645026482916"),
            new BigInteger("47667343854657281903"),
            new BigInteger("8595902006365044063"),
            new BigInteger("1163297957344668388"),
            new BigInteger("117656387352093658"),
            new BigInteger("8867391802663976"),
            new BigInteger("496969357462633"),
            new BigInteger("20680885154299"),
            new BigInteger("638331848991"),
            new BigInteger("14602316184"),
            new BigInteger("247426747"),
            new BigInteger("3104126"),
            new BigInteger("28824"),
            new BigInteger("198"),
            new BigInteger("1")
    };


    private static final long[] CC = {
            0x00000004741183A3L,
            0x00000036548CFC06L,
            0x0000024FDCBF140AL,
            0x0000171D939DE045L,
            0x0000D00CF58F6F84L,
            0x000680681CF796E3L,
            0x002D82D8305B0FEAL,
            0x011111110E066FD0L,
            0x0555555555070F00L,
            0x155555555581FF00L,
            0x400000000002B400L,
            0x7FFFFFFFFFFF4800L,
            0x7fffffffffffffffL,
    };


    private static int baseSampler(){
        byte[] randomBytes = new byte[9];
        secureRandom.nextBytes(randomBytes);
        BigInteger u = new BigInteger(1, randomBytes);
        return binarySearch(0, 17, u);
    }

    private static long approxExp(double x, double ccs){
        long yy = CC[0];
        long zz = (long) Math.floor(x * Long.MAX_VALUE + x);
        for(int i = 1;i < 12;i++){
            yy = CC[i] - multiplyAndShift(zz, yy, 63);
        }
        yy = CC[12] - multiplyAndShift(zz, yy, 63) + 1;
        zz = (long) Math.floor(ccs * Long.MAX_VALUE + ccs);
        yy = multiplyAndShift(zz, yy, 63);
        return yy;
    }

    private static boolean berExp(double x, double ccs){
        int s = (int) Math.floor(x / LN2);
        double r = x - s * LN2;
        s = Math.min(s, 63);
        long a = approxExp(r, ccs);
        int i = 64;
        long ww;
        long highPart = (a >>> 63);
        long lowPart = a << 1;

        highPart -= (lowPart == 0 ? 1 : 0);
        lowPart -= 1;
        do{
            i -= 8;
            int rand = secureRandom.nextInt(256);
            ww = rand - (((highPart << (64 - (s + i))) | (lowPart >>> (s + i))) & 0xFF);
        } while (ww == 0 && i > 0);
        return ww < 0;
    }

    public static int samplerZ(double u, double sigMin, double sigPrime){
        double r = u - Math.floor(u);
        double ccs = sigMin / sigPrime;
        double sigma2Sqrt = 2.0 * sigPrime * sigPrime;
        while(true){
            int z0 = baseSampler();
            int b = secureRandom.nextInt(256) & 0x1;
            int z = b + (2 * b - 1) * z0;
            double t = z - r;
            double x = t * t / sigma2Sqrt - z0 * z0 / SIG_MAX_2_SQRT;
            if(berExp(x, ccs)){
                return z + (int) Math.floor(u);
            }
        }
    }

    private static long multiplyAndShift(long a, long b, int shift) {
        long aLow = a & 0xFFFFFFFFL;
        long aHigh = a >>> 32;
        long bLow = b & 0xFFFFFFFFL;
        long bHigh = b >>> 32;

        long lowLow = aLow * bLow;
        long lowHigh = aLow * bHigh;
        long highLow = aHigh * bLow;
        long highHigh = aHigh * bHigh;

        long mid1 = (lowLow >>> 32) + (lowHigh & 0xFFFFFFFFL) + (highLow & 0xFFFFFFFFL);
        long mid2 = (mid1 >>> 32) + (lowHigh >>> 32) + (highLow >>> 32) + highHigh;

        long lowPart = (lowLow & 0xFFFFFFFFL) | ((mid1 & 0xFFFFFFFFL) << 32);

        return (mid2 << (64 - shift)) | (lowPart >>> shift);
    }

    private static int binarySearch(int l, int r, BigInteger target){
        while(l <= r){
            int mid = (l + r) >>> 1;
            int cmp = target.compareTo(RCDT[mid]);
            if(cmp > 0){
                r = mid - 1;
            }else if(cmp < 0){
                l = mid + 1;
            }else{
                return mid;
            }
        }
        return l;
    }

}
