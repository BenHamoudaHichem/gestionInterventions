package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.material.ECategory;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.team.Status;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.intervention.InterventionRepositoryImpl;
import com.app.gestionInterventions.services.FileUploadService;
import com.app.gestionInterventions.services.GeocodeService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/materials")
public class MaterialController implements IResource<Material> {
    @Autowired
    MaterialRepositoryImpl materialRepository;
    @Autowired
    InterventionRepositoryImpl interventionRepository;
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    GeocodeService geocodeService;

    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> create(Material material, BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        material.getAddress().setLocation(geocodeService.fromCity(material.getAddress()));
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
        material.getAddress().setLocation(geocodeService.fromCity(material.getAddress()));

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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Material>> all(Map<String, String> args)  {
        HashMap<String, String> map= new HashMap<>(args);
        if (map.containsKey("status")&&map.get("status").equals(Status.Available.name())) {
            try {
                return ResponseEntity.ok().body(materialRepository.availableMaterials());
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.ok().body(new ArrayList<Material>());
            }
        }
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
            List<Material> res =this.materialRepository.all().orElse(new ArrayList<>());
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
        List<Material>res=materialRepository.searchAnd(args,sort).orElse(new ArrayList<>());
        try {
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));
            return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
        }catch (IllegalArgumentException ex)
        {
            headers.set("totalPages", String.valueOf(-1));
            headers.set("totalResults", String.valueOf(0));
            return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
        }
        catch (IndexOutOfBoundsException ex)
        {
            headers.set("totalPages", String.valueOf(-1));
            headers.set("totalResults", String.valueOf(0));
            return ResponseEntity.ok().headers(headers).body(new ArrayList<>());
        }
    }


    @Override
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Material> findById(String id) throws ResourceNotFoundException {
        HttpHeaders headers= new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "inIntervention");
        headers.add("inIntervention",Boolean.toString(false));
        Material material=materialRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        Optional<Intervention> optionalIntervention = interventionRepository.findInterventionByMaterial(material);
        if (material.getCategory().equals(ECategory.Material)&&optionalIntervention.isPresent()) {
            headers.set("inIntervention",Boolean.toString(true));
            headers.set("Access-Control-Expose-Headers", "inIntervention,Address");
            headers.add("Address",optionalIntervention.get().getAddress().toString());

        }
        return ResponseEntity.ok().headers(headers).body(material);
    }

    @PostMapping("/file")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MessageResponse> create(@RequestParam("file") MultipartFile file) throws IOException {
        File dest = new File("src\\main\\resources\\dest.json");
        PrintWriter writer = new PrintWriter(dest);
        writer.print("");
        writer.close();
        try (OutputStream os = new FileOutputStream(dest)) {
            os.write(file.getBytes());
        }
       this.materialRepository.create(fileUploadService.serialize(dest,Material.class));
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre fichier est entregistré avec succès "));
    }
}
