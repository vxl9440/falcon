package edu.rit.encoding;

import edu.rit.entity.NTRUPolynomials;
import edu.rit.entity.PublicKey;
import edu.rit.entity.Signature;
import edu.rit.exception.InvalidHeaderException;
import edu.rit.transform.NTT;
import edu.rit.utils.Common;
import edu.rit.utils.Parameter;

import java.util.Arrays;

import static edu.rit.polynomial.Operation.*;
import static edu.rit.utils.Common.*;


public class Encoder {

    private final int sByteLen;
    private final int secretBitLen;

    public Encoder(int n, int sByteLen) {

        if(!Common.isValidDegree(n)){
            throw new IllegalArgumentException("Invalid degree n must be between [2, 1024], and is power of 2");
        }

        this.sByteLen = sByteLen;
        if(n >= 2 && n <= 32){
            this.secretBitLen = 8;
        }else if(n >= 64 && n <= 128){
            this.secretBitLen = 7;
        }else if(n >= 256 && n <= 512){
            this.secretBitLen = 6;
        }else{
            this.secretBitLen = 5;
        }
    }

    public byte[] encodeSignature(Signature sig) {
        int header = 0;
        header |= 16;
        header |= 32;
        int n = sig.s.length;
        int bitPower = Integer.numberOfTrailingZeros(n);
        header |= bitPower;
        byte[] compressedSign = Huffman.compress(sig.s, 8 * this.sByteLen - 328);
        if(compressedSign == null){
            return null;
        }
        int totalLength = 1 + compressedSign.length + 40;
        byte[] byteArray = new byte[totalLength];
        byteArray[0] = (byte) header;
        System.arraycopy(sig.salt, 0, byteArray, 1, Parameter.SALT_LEN);
        System.arraycopy(compressedSign, 0, byteArray, totalLength - compressedSign.length, compressedSign.length);
        return byteArray;
    }

    public Signature decodeSignature(byte[] byteArray) {
        int header = byteArray[0] & 0xFF;
        if((header >> 7) != 0 || ((header >> 4) & 1) != 1){ // not a valid header
            throw new InvalidHeaderException("Invalid signature header");
        }

//        int bit6 = (header >> 5) & 1;
//        int bit5 = (header >> 4) & 1;

        int power = header & 0b1111;

        int n = (1 << power);

        byte[] salt = new byte[Parameter.SALT_LEN];
        byte[] compressedSign = new byte[byteArray.length - 41];

        System.arraycopy(byteArray, 1, salt, 0, 40);
        System.arraycopy(byteArray, Parameter.SALT_LEN + 1, compressedSign, 0, compressedSign.length);

        int[] decompressSign = Huffman.decompress(compressedSign, 8 * this.sByteLen - 328, n);

        return new Signature(salt, decompressSign);
    }

    public byte[] encodePublicKey(PublicKey publicKey) {
        int n = publicKey.h.length;
        StringBuilder sb = new StringBuilder();
        for (int v : publicKey.h) {
            sb.append(toBitString(v, 14));
        }

        byte[] encodedPublicKey = new byte[(n * 14 + 8) >> 3];
        encodedPublicKey[0] = (byte) Integer.numberOfTrailingZeros(n);
        for (int i = 0; i < encodedPublicKey.length - 1; i++) {
            int t = Integer.parseInt(sb.substring(i << 3, (i + 1) << 3), 2);
            encodedPublicKey[i + 1] = (byte) t;
        }
        return encodedPublicKey;
    }

    public PublicKey decodePublicKey(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        int header = byteArray[0] & 0xFF;
        if((header >> 4) != 0){
            throw new InvalidHeaderException("Invalid public key header");
        }
        int n = 1 << header;
        for (int i = 1;i < byteArray.length;i++) {
            sb.append(toBitString(byteArray[i] & 0xFF, 8));
        }

        int[] h = new int[n];

        for(int i = 0;i < n;i++){
            h[i] = Integer.parseInt(sb.substring(i * 14, (i + 1) * 14), 2);
        }
        return new PublicKey(h);
    }

    public byte[] encodePrivateKey(NTRUPolynomials polys) {
        int n = polys.f.length;
        int header = 0;
        header |= 16;
        header |= 64;
        header |= Integer.numberOfTrailingZeros(n);

        int bitLen = this.secretBitLen;
        int sizeT = (bitLen * 2 * n) >> 3;
        int totalLen = 1 + sizeT + n;
        byte[] encodedPrivateKey = new byte[totalLen];
        encodedPrivateKey[0] = (byte) header;
        StringBuilder sb = new StringBuilder();
        for(int v : polys.f){
            String bitStr = toBitString(Math.abs(v), bitLen);
            bitStr = (v < 0 ? "1" : "0") + bitStr.substring(1);
            sb.append(bitStr);
        }

        for(int v : polys.g){
            String bitStr =  toBitString(Math.abs(v), bitLen);
            bitStr = (v < 0 ? "1" : "0") + bitStr.substring(1);
            sb.append(bitStr);
        }

        for(int i = 0;i < sizeT;i++){
            int t = Integer.parseInt(sb.substring(i << 3, (i + 1) << 3), 2);
            encodedPrivateKey[i + 1] = (byte) t;
        }

        for(int i = 0;i < n;i++){
//            byte b = (byte) polys.F[i];
//            System.out.println((b & 0xFF) == polys.F[i]);
            encodedPrivateKey[i + sizeT + 1] = (byte) polys.F[i];
        }
        return encodedPrivateKey;
    }

    public NTRUPolynomials decodePrivateKey(byte[] byteArray) {
        int header = byteArray[0] & 0xFF;
        if((header >> 4) != 5){ // invalid header
            throw new InvalidHeaderException("Invalid private key header");
        }
        int bitPower = header & 0b1111;
        int n = 1 << bitPower;
        int bitLen = this.secretBitLen;
        int sizeT = (bitLen * 2 * n) >> 3;
        int[] f = new int[n];
        int[] g = new int[n];
        int[] F = new int[n];

        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < sizeT;i++) {
            String str = toBitString(byteArray[i + 1] & 0xFF, 8);
            sb.append(str);
        }

        int curr = 0;
        for(int i = 0;i < n;i++){
            String bitStr = sb.substring(curr + i * bitLen, curr +  (i + 1) * bitLen);
            int sign = 1;
            if(bitStr.charAt(0) == '1'){
                sign = -1;
                bitStr = "0" + bitStr.substring(1);
            }
            f[i] = Integer.parseInt(bitStr, 2) * sign;
        }

        curr += bitLen * n;

        for(int i = 0;i < n;i++){
            String bitStr = sb.substring(curr + i * bitLen, curr +  (i + 1) * bitLen);
            int sign = 1;
            if(bitStr.charAt(0) == '1'){
                sign = -1;
                bitStr = "0" + bitStr.substring(1);
            }
            g[i] = Integer.parseInt(bitStr, 2) * sign;
        }

        for(int i = 0;i < n;i++){
            F[i] = byteArray[i + sizeT + 1];
        }

        int[] q = new int[n];
        Arrays.fill(q, 0);
        q[0] = Parameter.q;
        int[] qNTT = NTT.ntt(q);
        int[] fNTT = NTT.ntt(f);
        int[] gNTT = NTT.ntt(g);
        int[] FNTT = NTT.ntt(F);

        int[] GNTT = nttDivideQ(nttAdd(qNTT, nttMultiply(gNTT, FNTT)), fNTT);
        int[] G = NTT.intt(GNTT);

        for(int i = 0;i < n;i++){
            G[i] = (G[i] + (Parameter.q >> 1)) % Parameter.q - (Parameter.q >> 1);
        }

        return new NTRUPolynomials(f, g, F, G);
    }
}
