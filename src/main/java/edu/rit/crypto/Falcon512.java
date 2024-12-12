package edu.rit.crypto;

import edu.rit.utils.Parameter;

public class Falcon512 extends Falcon{
    public Falcon512(){
        super(512,  Parameter.SIG_MIN_512, Parameter.BETA_SQRT_512, Parameter.S_BYTE_LEN_512, Parameter.SD_512);
    }
}
