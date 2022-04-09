package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.payload.response.MessageResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface IResource<T> {
    @PostMapping(value = "",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<MessageResponse> create(@RequestBody @Valid T t, BindingResult bindingResult) throws EntityValidatorException;
    @PutMapping(value = "/{id}",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<MessageResponse>update(@PathVariable(value = "id") String id , @RequestBody @Valid T t, BindingResult bindingResult)throws ResourceNotFoundException,EntityValidatorException;
    @DeleteMapping(value = "/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<MessageResponse>delete(@PathVariable(value = "id") String id)throws ResourceNotFoundException;
    @GetMapping(value = "",produces = {MediaType.APPLICATION_JSON_VALUE})
    List<T> all(@RequestParam Map<String,String> allParams) throws ResourceNotFoundException;
    @GetMapping(value = "/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
    T findById(@PathVariable(value = "id",required = true) String id)throws ResourceNotFoundException;

}
