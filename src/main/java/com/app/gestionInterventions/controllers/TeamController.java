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
import org.springframework.http.HttpHeaders;
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
    @PreAuthorize("hasRole('MANAGER')")
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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> delete(String id) {
        if (this.teamRepository.detele(id)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.OK,"Equipe supprimée avec succés"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de suppression de l'équipe"));

    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Team>> all(Map<String, String> args)  {
        int page;
        int size;
        try {
            page=args.containsKey("page")?Integer.parseInt(args.remove("page")):0;
        }catch (NumberFormatException numberFormatException)
        {
            page=0;
        }
        try {
            size=args.containsKey("size")?Integer.parseInt(args.remove("size")):10;

        }catch (NumberFormatException numberFormatException)
        {
            size=10;
        }
        String order= args.containsKey("direction")?args.remove("direction"):"desc";
        String property= args.containsKey("property")?args.remove("property"):"createdAt";
        Sort sort= Sort.by(order.equals("asc")?Sort.Direction.ASC : Sort.Direction.DESC,property);
        Pageable pageable=  PageRequest.of(page,size,sort);
        int start = (int) pageable.getOffset();
        int end;
        HttpHeaders headers= new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "page,size,totalPages,totalResults");
        headers.add("page",String.valueOf(pageable.getPageNumber()));
        headers.add("size",String.valueOf(pageable.getPageSize()));
        if(args.isEmpty())
        {
            List<Team> res =this.teamRepository.all().orElse(new ArrayList<>());
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));

            try {
                return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
            }catch (IllegalArgumentException ex)
            {
                return ResponseEntity.ok().headers(headers).body(res);
            }catch (IndexOutOfBoundsException ex)
            {
                return ResponseEntity.ok().headers(headers).body(res);
            }
        }
        List<Team> res = new ArrayList<Team>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            res.addAll(this.teamRepository.search(e.getKey(),e.getValue()).orElse(new ArrayList<>()));
        }
        try {
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));
            return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
        }catch (IllegalArgumentException ex)
        {
            return ResponseEntity.ok().headers(headers).body(res);
        }catch (IndexOutOfBoundsException ex)
        {
            return ResponseEntity.ok().headers(headers).body(res);
        }

    }


    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Team> findById(String id) throws ResourceNotFoundException {
        HttpHeaders headers= new HttpHeaders();
        return ResponseEntity.ok().body(this.teamRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }
}
