package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.work.intervention.category.CategoryRepositoryImpl;
import com.sun.net.httpserver.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/categories")
public class CategoryController implements IResource<Category> {
    @Autowired
    CategoryRepositoryImpl categoryRepository;

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> create(Category category, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        this.categoryRepository.create(category);
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Cette category est enregistrée avec succes")) ;
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> update(String id, Category category, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if(this.categoryRepository.update(id,category)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Cette category est enregistrée avec succes")) ;
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de modification")) ;
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> delete(String id) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Category>> all(Map<String, String> allParams)  {

        HttpHeaders headers= new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "totalResults");
        if(allParams.isEmpty())
        {
            headers.add("totalResults",String.valueOf(this.categoryRepository.all().orElse(new ArrayList<>()).size()));
            return ResponseEntity.ok().headers(headers).body(this.categoryRepository.all().orElse(new ArrayList<>()));
        }
        List<Category> res = new ArrayList<Category>();
        for (Map.Entry<String,String> e:
                allParams.entrySet()) {
            res.addAll(this.categoryRepository.search(e.getKey(),e.getValue()).orElse(new ArrayList<>()));
        }
        headers.add("totalResults",String.valueOf(res.size()));
        return ResponseEntity.ok().headers(headers).body(res);
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Category> findById(String id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(this.categoryRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }
    @GetMapping("/{id}/interventions")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Intervention> interventionsPerCategory(@PathVariable(value = "id")String id) throws ResourceNotFoundException {
        return this.categoryRepository.findInterventionsByCategory(id).orElseThrow(ResourceNotFoundException::new);
    }
}
