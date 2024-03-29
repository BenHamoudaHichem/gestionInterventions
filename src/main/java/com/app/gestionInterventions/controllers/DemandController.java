package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.demand.Status;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.app.gestionInterventions.services.GeocodeService;
import com.sun.net.httpserver.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/demands")
public class DemandController implements IResource<Demand>  {
    @Autowired
    DemandRepositoryImpl demandRepository;
    @Autowired
    GeocodeService geocodeService;

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('CUSTOMER')")
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
    public ResponseEntity<List<Demand>> all(Map<String, String> args)  {
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
        Pageable pageable=  PageRequest.of(page,size);
        int start = (int) pageable.getOffset();
        int end;
        HttpHeaders headers= new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "page,size,totalPages,totalResults");
        headers.add("page",String.valueOf(pageable.getPageNumber()));
        headers.add("size",String.valueOf(pageable.getPageSize()));


        if(args.isEmpty())
        {
            List<Demand> res =this.demandRepository.all().orElse(new ArrayList<>());
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));

            try {
                return ResponseEntity.ok().headers(headers).body(new PageImpl<Demand>(res.subList(start, end), pageable, res.size()).getContent());
            }catch (IllegalArgumentException ex)
            {
                headers.set("totalPages", String.valueOf(-1));
                headers.set("totalResults", String.valueOf(0));
                return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
            }catch (IndexOutOfBoundsException ex)
            {
                headers.set("totalPages", String.valueOf(-1));
                headers.set("totalResults", String.valueOf(0));
                return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
            }
        }
        List<Demand> res =demandRepository.searchAnd(args,sort).orElse(new ArrayList<>());
        try {
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));
            end = Math.min((start + pageable.getPageSize()), res.size());
            return ResponseEntity.ok().headers(headers).body(new PageImpl<Demand>(res.subList(start, end), pageable, res.size()).getContent());
        }catch (IllegalArgumentException ex)
        {
            return ResponseEntity.ok().headers(headers).body(res);
        }catch (IndexOutOfBoundsException ex)
        {
            return ResponseEntity.ok().headers(headers).body(res);
        }
    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('CUSTOMER')")
    public ResponseEntity<Demand> findById(String id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(this.demandRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('TEAMMANAGER') or hasRole('CUSTOMER')")
    public List<Demand> findByUser(@PathVariable(value = "id")String id) throws ResourceNotFoundException {
        return this.demandRepository.allByUser(id).orElseThrow(ResourceNotFoundException::new);
    }
    @GetMapping("/test")
    public List<Demand> gettest() throws ResourceNotFoundException {
        Pageable pageable=  PageRequest.of(0,100, Sort.by(Sort.Direction.DESC,"createdAt"));
        List<Demand>demands=demandRepository.all().orElseThrow(ResourceNotFoundException::new);
        int start = (int) pageable.getOffset();
        int end =(start + pageable.getPageSize()) > demandRepository.all().get().size() ? demandRepository.all().get().size(): (start + pageable.getPageSize());
        try {
            return new PageImpl<Demand>(demands.subList(start, end), pageable, demands.size()).getContent();
        }catch (IllegalArgumentException ex)
        {
            return new ArrayList<Demand>();
        }
    }

}
