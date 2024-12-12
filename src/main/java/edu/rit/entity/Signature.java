package edu.rit.entity;

public class Signature {
    public byte[] salt;
    public int[] s;
    public Signature(byte[] salt, int[] s) {
        this.salt = salt;
        this.s = s;
    }
}
