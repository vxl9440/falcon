package edu.rit.utils;

public class Common {

    public static boolean isValidDegree(int n){
        return (n >= 2 && n <= 1024) && Integer.bitCount(n) == 1;
    }

    public static String toBitString(int v, int bitLen){
        String bitStr = Integer.toBinaryString(v);
        if(bitStr.length() < bitLen){
            return "0".repeat(bitLen - bitStr.length()) + bitStr;
        }else{
            return bitStr;
        }
    }

    public static void normalizeQ(int[] f){
        for(int i = 0;i < f.length;i++){
            f[i] = (f[i] + (Parameter.q >> 1)) % Parameter.q - (Parameter.q >> 1);
        }
    }
}


