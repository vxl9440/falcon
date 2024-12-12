package edu.rit.exception;

public class InvalidHeaderException extends RuntimeException{
    public InvalidHeaderException(String message) {
        super(message);
    }
}
