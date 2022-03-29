package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
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

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/teams")
public class TeamController implements IResource<Team> {

    @Autowired
    TeamRepositoryImpl teamRepository;


    @Override
    public ResponseEntity<MessageResponse> create(Team team, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (this.teamRepository.create(team).isPresent())
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Equipe enregistrée avec succés"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur d'enregistrer de cet équipe"));

    }

    @Override
    public ResponseEntity<MessageResponse> update(String id, Team team, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (this.teamRepository.update(id,team)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Equipe modifiée avec succés"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur de modification de l'équipe"));
    }

    @Override
    public ResponseEntity<MessageResponse> delete(String id) {
        if (this.teamRepository.detele(id)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.OK,"Equipe supprimée avec succés"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de suppression de l'équipe"));

    }

    @Override
    public List<Team> all(Map<String, String> args) throws ResourceNotFoundException {
        if(args.isEmpty())
        {
            return this.teamRepository.all().orElseThrow(ResourceNotFoundException::new);
        }
        List<Team> res = new ArrayList<Team>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            res.addAll(this.teamRepository.search(e.getKey(),e.getValue()).orElse(null));
        }
        if (res.isEmpty()){
            throw new ResourceNotFoundException();
        }
        return res;
    }


    @Override
    public Team findById(String id) throws ResourceNotFoundException {
        return this.teamRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
