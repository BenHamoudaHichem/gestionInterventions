package com.app.gestionInterventions.payload.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class MessageResponse {
    private boolean status;
    private HttpStatus httpStatus;
    private String message;
    private int code;
    private final String path;

    public MessageResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.status = !(this.httpStatus.is4xxClientError()||this.httpStatus.is5xxServerError());
        this.code = this.httpStatus.value();
        this.path= ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString().substring(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString().length());
    }

    public boolean isStatus() {
        return status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }
}
