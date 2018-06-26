package com.cognizant.samples.ai.instructions.endpoint;

import com.cognizant.samples.ai.ApplicationException;
import com.cognizant.samples.ai.instructions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CommonErrorHandlersAdvice  extends ResponseEntityExceptionHandler {

    @Value("${errors.general}")
    private String generalErrorMessage;


    @ExceptionHandler(value
            = { ApplicationException.class})
    protected ResponseEntity<Object> handleClientErrors(
            ApplicationException ex, WebRequest request) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("code", ex.code());
        response.put("message", ex.description());
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler(value
            = { Exception.class})
    protected ResponseEntity<Object> handleServerErrors(
            Exception ex, WebRequest request) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("code", "general");
        response.put("message", generalErrorMessage);
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
