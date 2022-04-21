package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.team.Status;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.payload.request.RegisterUserRequest;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
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
import java.util.*;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepositoryImpl userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TeamRepositoryImpl teamRepository;

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
        User user = new User(
                null,registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getIdentifier(),
                encoder.encode(registerRequest.getPassword()),
                registerRequest.getAdresse(),
                registerRequest.getTel()
        );


        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "manager":
                        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(managerRole);

                        break;
                    case "member":
                        Role memberRole = roleRepository.findByName(ERole.ROLE_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(memberRole);

                        break;
                    case "tm":
                        Role tmRole = roleRepository.findByName(ERole.ROLE_TEAMMANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(tmRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
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
    public ResponseEntity<MessageResponse> updateOne (@PathVariable(value = "id") String id ,@RequestBody @Valid RegisterUserRequest registerRequest,BindingResult bindingResult) throws EntityValidatorException {
        if (bindingResult.hasErrors()||bindingResult.hasFieldErrors())
        {
            throw new EntityValidatorException(bindingResult.getFieldErrors().get(0).getField()+" : "+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        registerRequest.getAdresse().setLocation(geocodeService.fromCity(registerRequest.getAdresse()));
        User user = new User(
                null,registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getIdentifier(),
                (registerRequest.getPassword()),
                registerRequest.getAdresse(),
                registerRequest.getTel()
        );


        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "manager":
                        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(managerRole);

                        break;
                    case "member":
                        Role memberRole = roleRepository.findByName(ERole.ROLE_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(memberRole);

                        break;
                    case "tm":
                        Role tmRole = roleRepository.findByName(ERole.ROLE_TEAMMANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(tmRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        if (this.userRepository.update(id,user)>0)
        {
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"User modifiée avec succés"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Erreur de modification"));

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
    public List<User>all(@RequestParam Map<String, String> args) throws ResourceNotFoundException {
        if(args.isEmpty())
        {
            return this.userRepository.all().orElseThrow(ResourceNotFoundException::new);
        }
        if(args.containsKey("role")&&args.containsValue(Status.Available.name()))
        {
            return this.teamRepository.availableMembers();
        }

        List<User> res = new ArrayList<User>();
        for (Map.Entry<String,String> e:
                args.entrySet()) {
            if (e.getKey().contains("role")) {
                Role role;
                switch (e.getValue()) {
                    case "manager":
                        role = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        break;
                    case "member":
                        role = roleRepository.findByName(ERole.ROLE_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        break;
                    case "tm":
                        role = roleRepository.findByName(ERole.ROLE_TEAMMANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        break;
                    default:
                        role = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                }
                res.addAll(this.userRepository.findByRoLe(role).orElse(new ArrayList<>()));
                continue;
            }

            res.addAll(this.userRepository.search(e.getKey(),e.getValue()).orElse(new ArrayList<>()));
        }
        if (res.isEmpty()){
            throw new ResourceNotFoundException();
        }
        return res;
    }

    @GetMapping(value = "/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public User findById(@PathVariable(value = "id",required = true) String id) throws ResourceNotFoundException {
      return userRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
