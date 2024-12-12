package edu.rit.entity;

import edu.rit.complex.SmallComplex;

public class FalconTree {
    public FalconTree left = null;
    public FalconTree right = null;
    public final SmallComplex[] value;

    public FalconTree(SmallComplex[] value) {
        this.value = value;
    }
}
