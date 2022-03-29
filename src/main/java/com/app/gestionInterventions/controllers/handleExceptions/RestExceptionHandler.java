package com.app.gestionInterventions.controllers.handleExceptions;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.mongodb.MongoWriteException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityValidatorException.class)
    protected ResponseEntity<MessageResponse> handleEntityValidatorException(
            EntityValidatorException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<MessageResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_FOUND,ex.getMessage()));
    }
}
