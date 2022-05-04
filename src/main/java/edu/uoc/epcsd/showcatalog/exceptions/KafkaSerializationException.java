package edu.uoc.epcsd.showcatalog.exceptions;

public class KafkaSerializationException extends RuntimeException {
    public KafkaSerializationException(String errorMessage) {
        super(errorMessage);
    }
}