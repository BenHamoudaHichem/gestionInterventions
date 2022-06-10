package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.EntityValidatorException;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.team.Status;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
    FileUploadService fileUploadService;
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
        this.userRepository.create(fileUploadService.serialize(dest, User.class));
        return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Users file registered successfully!"));
    }
    @GetMapping("")
    public ResponseEntity<List<User>>all(@RequestParam Map<String, String> args)  {
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
        Pageable pageable=  PageRequest.of(page,size,sort);
        int start = (int) pageable.getOffset();
        int end;
        HttpHeaders headers= new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "page,size,totalPages,totalResults");
        headers.add("page",String.valueOf(pageable.getPageNumber()));
        headers.add("size",String.valueOf(pageable.getPageSize()));
        List<User> res=new ArrayList<>();

        if(args.isEmpty())
        {
            res =this.userRepository.all().orElse(new ArrayList<>());
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));

            try {
                return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
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
        if(args.containsKey("role")&&args.containsKey("status")&&args.get("status").equals(Status.Available.name()))
        {
            res = this.teamRepository.availableMembers();
            args.remove("role");
            args.remove("status");
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));

            try {
                return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
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
        try {
            res=userRepository.searchAnd(args,sort).orElse(new ArrayList<>());
            end = Math.min((start + pageable.getPageSize()), res.size());
            headers.add("totalPages",String.valueOf(((res.size()/pageable.getPageSize())+Integer.compare(res.size()%pageable.getPageSize(),0))-1));
            headers.add("totalResults",String.valueOf(res.size()));

            return ResponseEntity.ok().headers(headers).body(new PageImpl<>(res.subList(start, end), pageable, res.size()).getContent());
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

    @GetMapping(value = "/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> findById(@PathVariable(value = "id",required = true) String id) throws ResourceNotFoundException {
      return ResponseEntity.ok().body(userRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }
}
