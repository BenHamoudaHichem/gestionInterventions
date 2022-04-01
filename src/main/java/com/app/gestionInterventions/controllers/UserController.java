package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.payload.request.RegisterUserRequest;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import com.app.gestionInterventions.security.jwt.JwtUtils;
import com.app.gestionInterventions.services.FileUploadService;
import com.app.gestionInterventions.services.GeocodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/users")
public class UserController  {

    @Autowired
    UserRepositoryImpl userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    GeocodeService geocodeService;

    @PostMapping(value = "",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest registerRequest, BindingResult bindingResult) throws EntityValidatorException {

        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        registerRequest.getAdresse().setLocation(geocodeService.fromCity(registerRequest.getAdresse()));
        User user = new User(registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getIdentifier(),
                encoder.encode(registerRequest.getPassword()),
                registerRequest.getAdresse(),
                registerRequest.getTel()
        );


        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "tm":
                        Role modRole = roleRepository.findByName(ERole.ROLE_TeamManager)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.create(user);
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,registerRequest.getFirstName()+", Vous etes maintenant insrit avec nous"));
    }
    @PutMapping(value = "/{id}",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MessageResponse> update (@PathVariable(value = "id") String id ,@RequestBody @Valid User user,BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (this.userRepository.update(id,user)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"User modifiée avec succés"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST,"Erreur de modification!"));
    }
    @PostMapping("/file")
    public ResponseEntity<MessageResponse> create(@RequestParam("file") MultipartFile file) throws IOException {
        File dest = new File("H:\\PFE\\Réalisation\\Nouveau dossier\\gestInertv\\gestInertv\\src\\main\\resources\\dest.json");
        PrintWriter writer = new PrintWriter(dest);
        writer.print("");
        writer.close();
        try (OutputStream os = new FileOutputStream(dest)) {
            os.write(file.getBytes());
        }
        this.userRepository.create(FileUploadService.loadMaterial(dest, User.class));
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Users file registered successfully!"));
    }
    @GetMapping("")
    public List<User>all() throws ResourceNotFoundException {
        return this.userRepository.all().orElseThrow(ResourceNotFoundException::new);
    }
}
