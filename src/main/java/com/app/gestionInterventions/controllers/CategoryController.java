package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.work.intervention.category.CategoryRepositoryImpl;
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
@RequestMapping("/api/categories")
public class CategoryController implements IResource<Category> {
    @Autowired
    CategoryRepositoryImpl categoryRepository;

    @Override
    public ResponseEntity<MessageResponse> create(Category category, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        this.categoryRepository.create(category);
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Cette category est enregistrée avec succes")) ;
    }

    @Override
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
    public ResponseEntity<MessageResponse> delete(String id) {
        return null;
    }

    @Override
    public List<Category> all(Map<String, String> allParams) throws ResourceNotFoundException {
        if(allParams.isEmpty())
        {
            return this.categoryRepository.all().orElseThrow(ResourceNotFoundException::new);
        }
        List<Category> res = new ArrayList<Category>();
        for (Map.Entry<String,String> e:
                allParams.entrySet()) {
            res.addAll(this.categoryRepository.search(e.getKey(),e.getValue()).orElse(null));
        }
        return res;
    }

    @Override
    public Category findById(String id) throws ResourceNotFoundException {
        return this.categoryRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
