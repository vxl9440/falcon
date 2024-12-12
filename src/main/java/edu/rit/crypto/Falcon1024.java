package edu.rit.crypto;

import edu.rit.utils.Parameter;

public class Falcon1024 extends Falcon{
    public Falcon1024(){
        super(1024,  Parameter.SIG_MIN_1024, Parameter.BETA_SQRT_1024, Parameter.S_BYTE_LEN_1024, Parameter.SD_1024);
    }
}
