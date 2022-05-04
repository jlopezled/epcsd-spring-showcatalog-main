package edu.uoc.epcsd.showcatalog.exceptions;


import org.springframework.validation.BindingResult;

public class InvalidDataException extends RuntimeException {

    private BindingResult result;

    public InvalidDataException(BindingResult result) {
        super();
        this.setResult(result);
    }

    public BindingResult getResult() {
        return result;
    }

    public void setResult(BindingResult result) {
        this.result = result;
    }

}

