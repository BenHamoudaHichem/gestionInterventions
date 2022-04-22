package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;
import com.app.gestionInterventions.services.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/materials")
public class MaterialController implements IResource<Material> {
    @Autowired
    MaterialRepositoryImpl materialRepository;
    @Autowired
    FileUploadService fileStorageService;

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> create(Material material, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        if (this.materialRepository.create(material).isPresent()){
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre materiel est enregistré avec succes"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.SERVICE_UNAVAILABLE,"Votre materiel n'est pas enregistré !"));
    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> update (String id,Material material, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if ( materialRepository.update(id,material)>0) {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED, "Votre Materiel est modifié avec succès"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.NOT_MODIFIED, "Erreur de modification du materiel !"));

    }

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> delete(String id) {
        if (this.materialRepository.detele(id)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.OK,"Votre materiel est supprimée avec succès"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse(HttpStatus.NOT_MODIFIED,"Erreur de suppression du materiel"));
    }

    @Override
    public List<Material> all(Map<String, String> args) throws ResourceNotFoundException {
        if(args.isEmpty())
        {
            return this.materialRepository.all().orElseThrow(ResourceNotFoundException::new);
        }
        List<Material> res = new ArrayList<Material>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            res.addAll(this.materialRepository.search(e.getKey(),e.getValue()).orElse(null));
        }

        if (res.isEmpty()){
            throw new ResourceNotFoundException();
        }

        return res;
    }


    @Override
    public Material findById(String id) throws ResourceNotFoundException {
        return materialRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping("/file")
    public ResponseEntity<MessageResponse> create(@RequestParam("file") MultipartFile file) throws IOException {


        File dest = new File("src\\main\\resources\\dest.json");
        PrintWriter writer = new PrintWriter(dest);
        writer.print("");
        writer.close();
        try (OutputStream os = new FileOutputStream(dest)) {
            os.write(file.getBytes());
        }
        this.materialRepository.create(FileUploadService.loadMaterial(dest,Material.class));
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre fichier est entregistré avec succès "));
    }
}
