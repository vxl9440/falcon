package edu.rit.complex;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class LargeComplex {
    public static final LargeComplex HALF = new LargeComplex(BigDecimal.valueOf(0.5), BigDecimal.ZERO);

    public BigDecimal r;
    public BigDecimal i;

    public LargeComplex(BigDecimal r, BigDecimal i){
        this.r = r;
        this.i = i;
    }

    public static LargeComplex add(LargeComplex c1, LargeComplex c2){
        return new LargeComplex(c1.r.add(c2.r), c1.i.add(c2.i));
    }

    public static LargeComplex subtract(LargeComplex c1, LargeComplex c2){
        return new LargeComplex(c1.r.subtract(c2.r), c1.i.subtract(c2.i));
    }

    public static LargeComplex multiply(LargeComplex c1, LargeComplex c2){
        BigDecimal rr = c1.r.multiply(c2.r).subtract(c1.i.multiply(c2.i));
        BigDecimal ri = c1.r.multiply(c2.i).add(c1.i.multiply(c2.r));
        return new LargeComplex(rr, ri);
    }

    public static LargeComplex divide(LargeComplex c1, LargeComplex c2){
        BigDecimal denominator = c2.r.multiply(c2.r).add(c2.i.multiply(c2.i));
        BigDecimal real = c1.r.multiply(c2.r).add(c1.i.multiply(c2.i));
        BigDecimal img = c1.i.multiply(c2.r).subtract(c1.r.multiply(c2.i));
        return new LargeComplex(real.divide(denominator, 16, RoundingMode.HALF_UP), img.divide(denominator, 16, RoundingMode.HALF_UP));
    }

    public LargeComplex add(LargeComplex c){
        return new LargeComplex(this.r.add(c.r), this.i.add(c.i));
    }

    public LargeComplex subtract(LargeComplex c){
        return new LargeComplex(this.r.subtract(c.r), this.i.subtract(c.i));
    }

    public LargeComplex multiply(LargeComplex c){
        BigDecimal rr = this.r.multiply(c.r).subtract(this.i.multiply(c.i));
        BigDecimal ri = this.r.multiply(c.i).add(this.i.multiply(c.r));
        return new LargeComplex(rr, ri);
    }

    public LargeComplex divide(LargeComplex c){
        BigDecimal denominator = c.r.multiply(c.r).add(c.i.multiply(c.i));
        BigDecimal real = this.r.multiply(c.r).add(this.i.multiply(c.i));
        BigDecimal img = this.i.multiply(c.r).subtract(this.r.multiply(c.i));
        return new LargeComplex(real.divide(denominator, 16, RoundingMode.HALF_UP), img.divide(denominator, 16, RoundingMode.HALF_UP));
    }

    public LargeComplex conjugate() {
        return new LargeComplex(this.r, this.i.negate());
    }

    public LargeComplex negate(){
        return new LargeComplex(this.r.negate(), this.i.negate());
    }

    public BigDecimal magnitude() {
        return this.r.multiply(this.r).add(this.i.multiply(this.i)).sqrt(MathContext.DECIMAL32);
    }

    public String toString(){
        return String.format("%f "+(this.i.compareTo(BigDecimal.ZERO) <= 0? "" : "+")+" %fj",this.r, this.i);
    }
}
