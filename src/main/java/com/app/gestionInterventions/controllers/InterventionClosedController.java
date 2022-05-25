package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.InterventionClosed;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.work.intervention.interventionClosed.InterventionClosedRepositoryImpl;
import com.sun.net.httpserver.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/interventionCloseds")
public class InterventionClosedController implements IResource<InterventionClosed> {
    @Autowired
    private InterventionClosedRepositoryImpl interventionClosedRepository;
    @Override
    public ResponseEntity<MessageResponse> create(InterventionClosed interventionClosed, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<InterventionClosed> interventionClosedOptional=this.interventionClosedRepository.create(interventionClosed);

        if (interventionClosedOptional.isPresent())
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre intervention est enregistrée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur d'enregistrement de l'intervention"));
    }

    @Override
    public ResponseEntity<MessageResponse> update(String id, InterventionClosed interventionClosed, BindingResult bindingResult) throws ResourceNotFoundException, EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        if (this.interventionClosedRepository.update(id,interventionClosed)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre intervention est modifiée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de modification de l'intervention"));
    }

    @Override
    public ResponseEntity<MessageResponse> delete(String id) throws ResourceNotFoundException {
        return null;
    }
    @Override
    public ResponseEntity<List<InterventionClosed>> all(Map<String, String> allParams) {

        return ResponseEntity.ok(this.interventionClosedRepository.all().orElse(new ArrayList<>()));
    }

    @Override
    public ResponseEntity<InterventionClosed> findById(String id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(this.interventionClosedRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }
}
