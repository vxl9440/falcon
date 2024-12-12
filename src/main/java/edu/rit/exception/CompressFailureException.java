package edu.rit.exception;

public class CompressFailureException extends RuntimeException{
    public CompressFailureException(String message) {
        super(message);
    }
}
