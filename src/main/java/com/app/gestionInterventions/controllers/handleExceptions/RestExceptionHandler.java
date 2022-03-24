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

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<MessageResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }
    @ExceptionHandler(MongoWriteException.class)
    protected ResponseEntity<MessageResponse> handleMongoWriteException(
            MongoWriteException ex) {
        if (ex.getMessage().contains("tel:")){
            return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,"ce numéro du télephone exist pour le moment!"));
        }
        if (ex.getMessage().contains("name:")){
            return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,"ce nom existe pour le moment!"));
        }
        if (ex.getMessage().contains("identifier:")){
            return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,"Cet identifiant du compte existe pour le moment!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<MessageResponse> handleAuthenticationException(
            AuthenticationException ex) {

        String msg= ex.getMessage();
        if(ex.getMessage().equals("Index 0 out of bounds for length 0"))
        {
            msg= "Bad credentials";
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.UNAUTHORIZED,msg));
    }
    @ExceptionHandler(IndexOutOfBoundsException.class)
    protected ResponseEntity<MessageResponse> handleUnknownUserName(
            IndexOutOfBoundsException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }
    @NotNull
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE,ex.getMessage()));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage()));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_FOUND,ex.getMessage()));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED,ex.getMessage()));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,ex.getMessage()));
    }

    @NotNull
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest webRequest) {
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.REQUEST_TIMEOUT,ex.getMessage()));

    }
}
