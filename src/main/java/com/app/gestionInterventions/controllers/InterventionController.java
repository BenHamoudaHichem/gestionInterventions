package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.team.Status;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.intervention.InterventionRepositoryImpl;
import com.app.gestionInterventions.services.GeocodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/interventions")
public class InterventionController implements IResource<Intervention> {
    @Autowired
    InterventionRepositoryImpl interventionRepository;
    @Autowired
    TeamRepositoryImpl teamRepository;
    @Autowired
    DemandRepositoryImpl demandRepository;
    @Autowired
    GeocodeService geocodeService;


    @Override
    public ResponseEntity<MessageResponse> create(Intervention intervention, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        intervention.getAddress().setLocation(geocodeService.fromCity(intervention.getAddress()));

        Optional<Intervention>interventionOptional=this.interventionRepository.create(intervention);

        if (interventionOptional.isPresent())
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre intervention est enregistrée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur d'enregistrement de l'intervention"));
    }

    @Override
    public ResponseEntity<MessageResponse> update(String id, Intervention intervention,BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        intervention.getAddress().setLocation(geocodeService.fromCity(intervention.getAddress()));

        if (this.interventionRepository.update(id,intervention)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre intervention est modifiée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de modification de l'intervention"));
    }

    @Override
    public ResponseEntity<MessageResponse> delete(String id) {
        if (this.interventionRepository.detele(id)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.OK,"Votre intervention est supprimée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.SERVICE_UNAVAILABLE,"Erreur de suppression de l'intervention"));
    }

    @Override
    public List<Intervention> all(Map<String, String> args) throws ResourceNotFoundException {
        int page=args.containsKey("page")?Integer.getInteger(args.remove("page")):0;
        int size=args.containsKey("size")?Integer.getInteger(args.remove("size")):this.interventionRepository.all().orElse(new ArrayList<>()).size();
        String order= args.containsKey("direction")?args.remove("direction"):"desc";
        String property= args.containsKey("property")?args.remove("property"):"createdAt";
        Sort sort= Sort.by(order.equals("asc")?Sort.Direction.ASC : Sort.Direction.DESC,property);
        Pageable pageable=  PageRequest.of(page,size,sort);
        int start = (int) pageable.getOffset();
        int end;
        if(args.isEmpty())
        {
            List<Intervention> res =this.interventionRepository.all().orElseThrow(ResourceNotFoundException::new);
            end = Math.min((start + pageable.getPageSize()), res.size());
            try {
                return new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent();
            }catch (IllegalArgumentException ex)
            {
                throw new ResourceNotFoundException("Pas de pages!");
            }
        }
        List<Intervention> res = new ArrayList<Intervention>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            res.addAll(this.interventionRepository.search(e.getKey(),e.getValue()).orElse(null));
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
    public Intervention findById(String id) throws ResourceNotFoundException {
        return this.interventionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
