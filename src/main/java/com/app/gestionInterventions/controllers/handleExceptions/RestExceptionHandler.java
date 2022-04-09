package com.app.gestionInterventions.controllers.handleExceptions;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.payload.response.MessageResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityValidatorException.class)
    protected ResponseEntity<MessageResponse> handleEntityValidatorException(
            EntityValidatorException ex) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<MessageResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.NOT_FOUND,ex.getMessage()));
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }
}
