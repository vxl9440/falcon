package edu.rit.crypto;

import edu.rit.complex.SmallComplex;
import edu.rit.encoding.Encoder;
import edu.rit.entity.*;
import edu.rit.generator.NTRUGenerator;
import edu.rit.polynomial.Algorithm;
import edu.rit.sampler.FFTSampling;
import edu.rit.sampler.GaussianSampler;
import edu.rit.transform.SmallFFT;
import edu.rit.utils.Common;
import edu.rit.utils.Mapper;
import edu.rit.utils.Parameter;
import org.bouncycastle.crypto.digests.SHAKEDigest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static edu.rit.polynomial.Operation.*;

public abstract class Falcon {
    protected final int n;
    protected final double sigMin;
    protected final int betaSquare;
    protected final int sByteLen;
    protected final double sd;
    private NTRUPolynomials NTRUPolys;
    private PrivateKey sk;
    private PublicKey pk;

    public Falcon(int n, double sigMin, int betaSquare, int sByteLen, double sd) {
        this.n = n;
        this.sigMin = sigMin;
        this.betaSquare = betaSquare;
        this.sByteLen = sByteLen;
        this.sd = sd;
        this.NTRUPolys = null;
        this.sk = null;
        this.pk = null;
    }

    public Falcon(Parameter param) {
        this(param.getDegree(), param.getSigMin(), param.getBetaSquare(), param.getsByteLen(), param.getSd());
    }

    public void keyGen(){
        NTRUGenerator gen = new NTRUGenerator(this.n, this.sigMin);
        this.NTRUPolys = gen.generate();

        double[] fd = Mapper.intToDouble(this.NTRUPolys.f);
        double[] gd = Mapper.intToDouble(this.NTRUPolys.g);
        double[] Fd = Mapper.intToDouble(this.NTRUPolys.F);
        double[] Gd = Mapper.intToDouble(this.NTRUPolys.G);
        SmallComplex[] ftNegate = SmallFFT.fft(polyNegate(fd));
        SmallComplex[] gt = SmallFFT.fft(gd);
        SmallComplex[] FtNegate = SmallFFT.fft(polyNegate(Fd));
        SmallComplex[] Gt = SmallFFT.fft(Gd);

        Basics B = new Basics(gt, ftNegate, Gt, FtNegate);

        SmallComplex[] bAdj00 = pfAdjoint(B.e00);
        SmallComplex[] bAdj01 = pfAdjoint(B.e10);
        SmallComplex[] bAdj10 = pfAdjoint(B.e01);
        SmallComplex[] bAdj11 = pfAdjoint(B.e11);

        Basics BP = new Basics(bAdj00, bAdj01, bAdj10, bAdj11);
        Basics G = Basics.multiply(B, BP);

        FalconTree t = FFTSampling.ffLDL(G);
        FFTSampling.normalize(t, this.sd);
        this.sk = new PrivateKey(B, t);
        int[] h = polyDivideQ(this.NTRUPolys.g, this.NTRUPolys.f);   // (g * f^-1) % q = (g / f) % q
        this.pk = new PublicKey(h);
    }

    private int[] hashToPoint(SHAKEDigest shake256){
        int kq = 61445;  // (2^16 // q) * q
        byte[] output = new byte[2];  // 16 bits
        int i = 0;
        int[] c = new int[n];
        while(i < this.n){
            shake256.doOutput(output, 0, 2);
            int t = ((output[0] & 0xFF) << 8) | (output[1] & 0xFF);
            if(t < kq){
                c[i++] = Math.floorMod(t, Parameter.q);
            }
        }
        return c;
    }

    private void shake256Update(File f, SHAKEDigest shake256){
        try (InputStream fis = new FileInputStream(f)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                shake256.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Signature sampleSignature(byte[] salt, int[] c){
        SmallComplex[] cFFT = SmallFFT.fft(Mapper.intToDouble(c));
        SmallComplex qInverse = new SmallComplex(1.0 / Parameter.q, 0.0);
        SmallComplex[] t0 = pfMultiply(pfScale(cFFT, qInverse), this.sk.B.e11);
        SmallComplex[] t1 = pfMultiply(pfScale(cFFT, qInverse.negate()), this.sk.B.e01);
        Pair<SmallComplex[]> t = new Pair<>(t0, t1);
        while (true){
            Pair<SmallComplex[]> z = FFTSampling.ffSampling(t, this.sk.T, this.sigMin);
            SmallComplex[] tz1 = pfSubtract(t.e1, z.e1);
            SmallComplex[] tz2 = pfSubtract(t.e2, z.e2);
            SmallComplex[] s1 = pfAdd(pfMultiply(tz1, this.sk.B.e00), pfMultiply(tz2, this.sk.B.e10));
            SmallComplex[] s2 = pfAdd(pfMultiply(tz1, this.sk.B.e01), pfMultiply(tz2, this.sk.B.e11));
            double sLen = pfInnerProduct(s1, s1) + pfInnerProduct(s2, s2);
            if(sLen <= betaSquare){
                int[] s2Int = Mapper.doubleToInt(SmallFFT.ifft(s2));
                return new Signature(salt, s2Int);
            }
        }
    }

    public Signature sign(byte[] message){
        if(this.sk == null){
            return null;
        }
        byte[] salt = new byte[Parameter.SALT_LEN]; // 320 bits
        GaussianSampler.secureRandom.nextBytes(salt);
        SHAKEDigest shake256 = new SHAKEDigest(256);
        shake256.update(salt, 0, Parameter.SALT_LEN);
        shake256.update(message, 0, message.length);
        int[] c = hashToPoint(shake256);
        return sampleSignature(salt, c);
    }

    public Signature sign(File f){
        if(!f.exists()){
            throw new IllegalArgumentException("File does not exist");
        }
        byte[] salt = new byte[Parameter.SALT_LEN]; // 320 bits
        GaussianSampler.secureRandom.nextBytes(salt);
        SHAKEDigest shake256 = new SHAKEDigest(256);
        shake256.update(salt, 0, Parameter.SALT_LEN);
        shake256Update(f, shake256);
        int[] c = hashToPoint(shake256);
        return sampleSignature(salt, c);
    }


    public Signature sign(String message){
        return sign(message.getBytes());
    }

    public boolean verify(byte[] message, Signature sig, PublicKey pk){
        SHAKEDigest shake256 = new SHAKEDigest(256);
        shake256.update(sig.salt, 0, Parameter.SALT_LEN);
        shake256.update(message, 0, message.length);
        int[] c = hashToPoint(shake256);
        int[] s2 = sig.s;
        if(s2 == null){
            return false;
        }
        int[] s1 = polySubtractQ(c, polyMultiplyQ(s2, pk.h));
        Common.normalizeQ(s1);
        return Algorithm.sqrtNorm(s1, s2) <= this.betaSquare;
    }

    public boolean verify(File f, Signature sig, PublicKey pk){
        SHAKEDigest shake256 = new SHAKEDigest(256);
        shake256.update(sig.salt, 0, Parameter.SALT_LEN);
        shake256Update(f, shake256);
        int[] c = hashToPoint(shake256);
        int[] s2 = sig.s;
        if(s2 == null){
            return false;
        }
        int[] s1 = polySubtractQ(c, polyMultiplyQ(s2, pk.h));
        return Algorithm.sqrtNorm(s1, s2) <= this.betaSquare;
    }

    public boolean verify(String message, Signature signature, PublicKey pk){
        return verify(message.getBytes(), signature, pk);
    }

    public Encoder getEncoder(){
        return new Encoder(this.n, this.sByteLen);
    }

    public PublicKey getPublicKey() {
        return this.pk;
    }

    public NTRUPolynomials getNTRUPolys(){
        return this.NTRUPolys;
    }
}
