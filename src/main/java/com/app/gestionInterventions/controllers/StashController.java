package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.tools.StashedRepository;
import com.app.gestionInterventions.services.CustomClassLoader;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.util.Introspection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/stasheds")
public class StashController {
    @Autowired
    private StashedRepository stashedRepository;
    private CustomClassLoader classLoader;

    @GetMapping("")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Stashed>> all(@RequestHeader("className") String className) throws ClassNotFoundException {
        List<Stashed> list =stashedRepository.allStashedByClass(classLoader.loadClass(StringUtils.capitalize(className))).orElse(new ArrayList<>());
        return ResponseEntity.ok().body(list);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> restore(@PathVariable(value = "id",required = true) String id) throws  ResourceNotFoundException {
        if(stashedRepository.restore(id).isPresent()){
            return  ResponseEntity.ok().body(new MessageResponse(HttpStatus.CREATED,"Restored"));
        }
        return  ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Problem"));
    }
    
}
