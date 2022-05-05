package com.app.gestionInterventions.controllers.handleExceptions;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.ws.client.WebServiceIOException;

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
    @ExceptionHandler(WebServiceIOException.class)
    protected ResponseEntity<MessageResponse> handleWebServiceIOException(
            WebServiceIOException ex) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage()));
    }
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<MessageResponse> handleBadCredentialsException(
            BadCredentialsException ex) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED,ex.getMessage()));
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<MessageResponse> handleMalformedJwtException(
            MalformedJwtException ex) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED,ex.getMessage()));
    }
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<MessageResponse> handleAuthenticationException(
            AuthenticationException ex) {
        if (
                ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString().substring(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString().length()).contains("/login")
        ) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED, "Invalide login"));

        }
        if (
                ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString().substring(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString().length()).contains("/logout")
        ) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED, "Seesion expirée, vous etes déconnecté"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED,ex.getMessage()));

    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    protected ResponseEntity<MessageResponse> handleIndexOutOfBoundsException(
            IndexOutOfBoundsException ex) {
        if (
                ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString().substring(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString().length()).contains("/login")
        ) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED,"Invalide login"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED,ex.getMessage()));
    }

    @ExceptionHandler(MongoWriteException.class)
    protected ResponseEntity<MessageResponse> handleMongoWriteException(
            MongoWriteException ex) {

        if( ex.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY))
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST,
                    StringUtils.substringBetween(ex.getError().getMessage(),"{","}").replace("identifier","email").replace("\"","")+"existe déja!"
            ));        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST,
                ex.getError().getMessage()
        ));
    }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.ok(new MessageResponse(HttpStatus.NOT_ACCEPTABLE,ex.getMessage()));
    }
}
