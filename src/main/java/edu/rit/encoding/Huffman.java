package edu.rit.encoding;


import edu.rit.exception.CompressFailureException;
import edu.rit.utils.Common;

public class Huffman {
    public static byte[] compress(int[] s, int sLen){
        StringBuilder sb = new StringBuilder();
        for (int j : s) {
            sb.append(j < 0 ? '1' : '0');
            sb.append(Common.toBitString(Math.abs(j) % (1 << 7), 7));
            int k = Math.abs(j) >> 7;
            sb.append("0".repeat(k));
            sb.append('1');
        }
        if(sLen < sb.length()){
            throw new CompressFailureException("Failed to compress sLen = " + sLen);
        }
        sb.append("0".repeat(sLen - sb.length()));
        byte[] byteData = new byte[sb.length() >> 3];
        for(int i = 0;i < byteData.length;i++){
            int t = Integer.parseInt(sb.substring(i << 3, (i + 1) << 3), 2);
            byteData[i] = (byte) t;
        }
        return byteData;
    }

    public static int[] decompress(byte[] byteData, int sLen, int n){
        if(byteData.length != (sLen >> 3)){
            throw new CompressFailureException("Failed to decompress sLen = " + sLen);
        }

        StringBuilder sb = new StringBuilder();
        for (byte val : byteData) {
            sb.append(Common.toBitString(val & 0xFF, 8));
        }
        char[] str = sb.toString().toCharArray();
        int[] s = new int[n];

        int cur = 0;
        for(int i = 0;i < n;i++){
            int sign = str[cur] == '0' ? 1 : -1;
            int si = 0;
            for(int j = 0;j < 7;j++){
                si += (str[cur + j + 1] - '0') << (6 - j);
            }
            int k = 0;
            while(str[cur + k + 8] == '0'){
                k++;
            }
            s[i] = sign * (si + (k << 7));
            if(s[i] == 0 && sign == -1){
                return null;
            }
            cur += 9 + k;
        }
        return s;
    }
}
