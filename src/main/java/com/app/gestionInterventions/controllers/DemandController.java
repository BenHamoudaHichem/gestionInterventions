package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.app.gestionInterventions.services.GeocodeService;
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
@RequestMapping("/api/demands")
public class DemandController implements IResource<Demand>  {
    @Autowired
    DemandRepositoryImpl demandRepository;
    @Autowired
    GeocodeService geocodeService;

    @Override
    public ResponseEntity<MessageResponse> create(Demand demand, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        demand.getAddress().setLocation(geocodeService.fromCity(demand.getAddress()));
        if (this.demandRepository.create(demand).isPresent())
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre demande est enregistrée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur d'enregistrement de la demande"));
    }

    @Override
    public ResponseEntity<MessageResponse> update(String id, Demand demand,BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (this.demandRepository.update(id,demand)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre demande est modifiée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de modification de la demande"));
    }

    @Override
    public ResponseEntity<MessageResponse> delete(String id) {
        if (this.demandRepository.detele(id)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.OK,"Votre demande est supprimée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur de suppression de la demande"));
    }

    @Override
    public List<Demand> all(Map<String, String> args) throws ResourceNotFoundException {
        if(args.isEmpty())
        {
            return this.demandRepository.all().orElseThrow(ResourceNotFoundException::new);
        }
        List<Demand> res = new ArrayList<Demand>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            res.addAll(this.demandRepository.search(e.getKey(),e.getValue()).orElse(null));
        }
        if (res.isEmpty()){
            throw new ResourceNotFoundException();
        }
        return res;
    }

    @Override
    public Demand findById(String id) throws ResourceNotFoundException {
        return this.demandRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
