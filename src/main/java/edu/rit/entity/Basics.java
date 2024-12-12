package edu.rit.entity;

import edu.rit.complex.SmallComplex;

import static edu.rit.polynomial.Operation.*;

public class Basics {
    public SmallComplex[] e00;
    public SmallComplex[] e01;
    public SmallComplex[] e10;
    public SmallComplex[] e11;

    public Basics(SmallComplex[] e00, SmallComplex[] e01, SmallComplex[] e10, SmallComplex[] e11) {
        this.e00 = e00;
        this.e01 = e01;
        this.e10 = e10;
        this.e11 = e11;
    }

    public static Basics multiply(Basics b1, Basics b2) {
        SmallComplex[] e00 = pfAdd(pfMultiply(b1.e00, b2.e00), pfMultiply(b1.e01, b2.e10));
        SmallComplex[] e01 = pfAdd(pfMultiply(b1.e00, b2.e01), pfMultiply(b1.e01, b2.e11));
        SmallComplex[] e10 = pfAdd(pfMultiply(b1.e10, b2.e00), pfMultiply(b1.e11, b2.e10));
        SmallComplex[] e11 = pfAdd(pfMultiply(b1.e10, b2.e01), pfMultiply(b1.e11, b2.e11));
        return new Basics(e00, e01, e10, e11);
    }
}
