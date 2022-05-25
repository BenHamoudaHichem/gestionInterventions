package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/teams")
public class TeamController implements IResource<Team> {

    @Autowired
    TeamRepositoryImpl teamRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepositoryImpl userRepository;


    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> create(Team team, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (this.teamRepository.create(team).isPresent())
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Equipe enregistrée avec succés"));
        }
        System.out.println(team);
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
        int page=args.containsKey("page")?Integer.getInteger(args.remove("page")):0;
        int size=args.containsKey("size")?Integer.getInteger(args.remove("size")):this.teamRepository.all().orElse(new ArrayList<>()).size();
        String order= args.containsKey("direction")?args.remove("direction"):"desc";
        String property= args.containsKey("property")?args.remove("property"):"createdAt";
        Sort sort= Sort.by(order.equals("asc")?Sort.Direction.ASC : Sort.Direction.DESC,property);
        Pageable pageable=  PageRequest.of(page,size,sort);
        int start = (int) pageable.getOffset();
        int end;
        if(args.isEmpty())
        {
            List<Team> res =this.teamRepository.all().orElseThrow(ResourceNotFoundException::new);
            end = Math.min((start + pageable.getPageSize()), res.size());
            try {
                return new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent();
            }catch (IllegalArgumentException ex)
            {
                throw new ResourceNotFoundException("Pas de pages!");
            }
        }
        List<Team> res = new ArrayList<Team>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            res.addAll(this.teamRepository.search(e.getKey(),e.getValue()).orElse(null));
        }
        if (res.isEmpty()){
            throw new ResourceNotFoundException();
        }
        try {
            end = Math.min((start + pageable.getPageSize()), res.size());
            return new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent();
        }catch (IllegalArgumentException ex)
        {
            throw new ResourceNotFoundException();
        }
    }


    @Override
    public Team findById(String id) throws ResourceNotFoundException {
        return this.teamRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
