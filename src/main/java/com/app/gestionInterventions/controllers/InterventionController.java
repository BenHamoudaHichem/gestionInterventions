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
import com.sun.net.httpserver.Headers;
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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> create(Intervention intervention, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors() || bindingResult.hasFieldErrors()) {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField() + " : " + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        intervention.getAddress().setLocation(geocodeService.fromCity(intervention.getAddress()));

        Optional<Intervention> interventionOptional = this.interventionRepository.create(intervention);

        if (interventionOptional.isPresent()) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED, "Votre intervention est enregistrée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.BAD_REQUEST, "Erreur d'enregistrement de l'intervention"));
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> update(String id, Intervention intervention, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors() || bindingResult.hasFieldErrors()) {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField() + " : " + bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        intervention.getAddress().setLocation(geocodeService.fromCity(intervention.getAddress()));

        if (this.interventionRepository.update(id, intervention) > 0) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED, "Votre intervention est modifiée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED, "Erreur de modification de l'intervention"));
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> delete(String id) {
        if (this.interventionRepository.detele(id) > 0) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.OK, "Votre intervention est supprimée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.SERVICE_UNAVAILABLE, "Erreur de suppression de l'intervention"));
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Intervention>> all(Map<String, String> args) {
        int page;
        int size;
        try {
            page = args.containsKey("page") ? Integer.parseInt(args.remove("page")) : 0;
        } catch (NumberFormatException numberFormatException) {
            page = 0;
        }
        try {
            size = args.containsKey("size") ? Integer.parseInt(args.remove("size")) : 10;

        } catch (NumberFormatException numberFormatException) {
            size = 10;
        }
        String order = args.containsKey("direction") ? args.remove("direction") : "desc";
        String property = args.containsKey("property") ? args.remove("property") : "createdAt";
        Sort sort = Sort.by(order.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, property);
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "page,size,totalPages,totalResults");
        headers.add("page", String.valueOf(pageable.getPageNumber()));
        headers.add("size", String.valueOf(pageable.getPageSize()));
        if (args.isEmpty()) {
            List<Intervention> res = this.interventionRepository.all().orElse(new ArrayList<>());
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages", String.valueOf(((res.size() / pageable.getPageSize()) + Integer.compare(res.size() % pageable.getPageSize(), 0)) - 1));
            headers.add("totalResults", String.valueOf(res.size()));

            try {
                return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
            } catch (IllegalArgumentException ex) {
                headers.set("totalPages", String.valueOf(-1));
                headers.set("totalResults", String.valueOf(0));
                return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
            } catch (IndexOutOfBoundsException ex) {
                headers.set("totalPages", String.valueOf(-1));
                headers.set("totalResults", String.valueOf(0));
                return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
            }
        }
        List<Intervention> res=interventionRepository.searchAnd(args,sort).orElse(new ArrayList<>());

        try {
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages", String.valueOf(((res.size() / pageable.getPageSize()) + Integer.compare(res.size() % pageable.getPageSize(), 0)) - 1));
            headers.add("totalResults", String.valueOf(res.size()));

            return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
        } catch (IllegalArgumentException ex) {
                headers.set("totalPages", String.valueOf(-1));
                headers.set("totalResults", String.valueOf(0));
                return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
        } catch (IndexOutOfBoundsException ex) {
                headers.set("totalPages", String.valueOf(-1));
                headers.set("totalResults", String.valueOf(0));
                return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
            }

    }

        @Override
        @PreAuthorize("hasRole('MANAGER')")
        public ResponseEntity<Intervention> findById(String id) throws ResourceNotFoundException {
            return ResponseEntity.ok().body(this.interventionRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
        }
    }