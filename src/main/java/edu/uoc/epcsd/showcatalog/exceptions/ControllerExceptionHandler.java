package edu.uoc.epcsd.showcatalog.exceptions;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * The type Controller exception handler allows us to consolidate our multiple,
 * scattered @ExceptionHandlers from before into a single, global error handling component.
 */
@ControllerAdvice
@ResponseBody
@Log4j2
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Not found exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<?> notFoundException(NotFoundException ex, WebRequest request) {
        StringBuilder bodyOfResponse = new StringBuilder();

        bodyOfResponse.append("Database error: ").append(ex.getMessage()).append("\n");
        log.error(bodyOfResponse.toString());
        return handleExceptionInternal(ex, bodyOfResponse.toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {DataIntegrityException.class})
    public ResponseEntity<?> DataIntegrityException(DataIntegrityException ex, WebRequest request) {
        StringBuilder bodyOfResponse = new StringBuilder();

        bodyOfResponse.append("Database error: ").append(ex.getMessage()).append("\n");
        log.error(bodyOfResponse.toString());
        return handleExceptionInternal(ex, bodyOfResponse.toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Invalid data exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {InvalidDataException.class})
    public ResponseEntity<?> invalidDataException(InvalidDataException ex, WebRequest request) {
        StringBuilder bodyOfResponse = new StringBuilder();

        List<FieldError> errors = ex.getResult().getFieldErrors();
        for (FieldError error : errors) {
            String errorLine = "Filed Name ::: " + error.getField() + " - Error Message :::" + error.getDefaultMessage();
            log.error(errorLine);
            bodyOfResponse.append(errorLine).append("\n");
        }

        return handleExceptionInternal(ex, bodyOfResponse.toString(), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

}
