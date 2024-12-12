package edu.rit.complex;


public class SmallComplex {
    public static final SmallComplex ONE = new SmallComplex(1.0, 0.0);
    public static final SmallComplex ZERO = new SmallComplex(0.0, 0.0);
    public static final SmallComplex HALF = new SmallComplex(0.5, 0.0);
    
    public double r;
    public double i;

    public SmallComplex(double r, double i){
        this.r = r;
        this.i = i;
    }

    public static SmallComplex add(SmallComplex c1, SmallComplex c2){
        return new SmallComplex(c1.r + c2.r, c1.i + c2.i);
    }

    public static SmallComplex subtract(SmallComplex c1, SmallComplex c2){
        return new SmallComplex(c1.r - c2.r, c1.i - c2.i);
    }

    public static SmallComplex multiply(SmallComplex c1, SmallComplex c2){
        return new SmallComplex(c1.r * c2.r - c1.i * c2.i, c1.r * c2.i + c2.r * c1.i);
    }

    public static SmallComplex divide(SmallComplex c1, SmallComplex c2){
        double denominator = c2.r * c2.r + c2.i * c2.i;
        double real = (c1.r * c2.r + c1.i * c2.i) / denominator;
        double img = (c1.i * c2.r - c1.r * c2.i) / denominator;
        return new SmallComplex(real, img);
    }

    public SmallComplex add(SmallComplex c){
        return new SmallComplex(this.r + c.r, this.i + c.i);
    }

    public SmallComplex subtract(SmallComplex c){
        return new SmallComplex(this.r - c.r, this.i - c.i);
    }

    public SmallComplex multiply(SmallComplex c){
        return new SmallComplex(this.r * c.r - this.i * c.i, this.r * c.i + c.r * this.i);
    }

    public SmallComplex divide(SmallComplex c){
        double denominator = c.r * c.r + c.i * c.i;
        double real = (this.r * c.r + this.i * c.i) / denominator;
        double img = (this.i * c.r - this.r * c.i) / denominator;
        return new SmallComplex(real, img);
    }

    public SmallComplex negate() {
        return new SmallComplex(-this.r, -this.i);
    }

    public Double magnitude() {
        return Math.sqrt(this.r * this.r + this.i * this.i);
    }

    public SmallComplex conjugate(){
        return new SmallComplex(this.r, -this.i);
    }

    @Override
    public String toString(){
        return String.format("%f "+(this.i < 0.0 ? "" : "+")+" %fj",this.r, this.i);
    }

}
