package edu.uoc.epcsd.showcatalog.exceptions;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String errorMessage) {
        super(errorMessage);
    }
}