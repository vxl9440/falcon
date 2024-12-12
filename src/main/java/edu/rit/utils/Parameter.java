package edu.rit.utils;

import edu.rit.exception.InvalidDegreeException;

public class Parameter {
    // common params
    public static final int q = 12289;
    public static final int PRECISION = 53;
    public static final int SALT_LEN = 40;
    public static final double SIG_MAX = 1.8205;


    //Falcon-512 params
    public static final double SD_512 = 165.736617183;
    public static final double SIG_MIN_512 = 1.277833697;
    public static final int BETA_SQRT_512 = 34034726;
    public static final int S_BYTE_LEN_512 = 666;

    //Falcon-1024 params
    public static final double SD_1024 = 168.388571447;
    public static final double SIG_MIN_1024 = 1.298280334;
    public static final int BETA_SQRT_1024 = 70265242;
    public static final int S_BYTE_LEN_1024 = 1280;

    private final int n;
    private final int lambda;
    private final double sd;
    private final int betaSquare;
    private final int sByteLen;
    private final double sigMin;

    public Parameter(int n) {

        if(!Common.isValidDegree(n)){
            throw new InvalidDegreeException("Invalid degree n must be between [2, 1024], and is power of 2");
        }

        this.n = n;
        if(n == 2 || n == 4){
            this.lambda = 2;
        }else{
            this.lambda = n >> 2;
        }

        if(n == 2){
            this.sByteLen = 44;
        }else if(n == 4){
            this.sByteLen = 47;
        }else if(n == 8){
            this.sByteLen = 52;
        }else if(n == 16){
            this.sByteLen = 63;
        }else if(n == 32){
            this.sByteLen = 82;
        }else if(n == 64){
            this.sByteLen = 122;
        }else if(n == 128){
            this.sByteLen = 200;
        }else if(n == 256){
            this.sByteLen = 356;
        }else if(n == 512){
            this.sByteLen = 666;
        }else{
            this.sByteLen = 1280;
        }

        this.sd = this.computeSD();
        this.betaSquare = this.computeBetaSquare();
        this.sigMin = this.computeSignMin();
    }

    private double computeSD(){
        double qs = Math.pow(2.0, 64);
        double epsilon = 1.0 / Math.sqrt(qs * this.lambda);
        return (1.0 / Math.PI) * Math.sqrt(Math.log((4.0 * n) * (1 * 1.0 / epsilon)) / 2.0)
                * 1.17 * Math.sqrt(Parameter.q);
    }

    private int computeBetaSquare(){
        double beta = 1.1 * this.sd * Math.sqrt(this.n << 1);
        return (int) (beta * beta);
    }

    private double computeSignMin(){
        return this.sd / (1.17 * Math.sqrt(Parameter.q));
    }

    public int getSecurityBit() {
        return lambda;
    }

    public int getDegree() {
        return n;
    }

    public double getSd() {
        return sd;
    }

    public int getBetaSquare() {
        return betaSquare;
    }

    public int getsByteLen() {
        return sByteLen;
    }

    public double getSigMin() {
        return sigMin;
    }

    @Override
    public String toString() {
        return "n = " + n + "\n" +
                "lambda = " + lambda + "\n" +
                "sd = " + sd + "\n" +
                "betaSquare = " + betaSquare + "\n" +
                "sigMin = " + sigMin + "\n" +
                "sByteLen = " + sByteLen + "\n";
    }
}
