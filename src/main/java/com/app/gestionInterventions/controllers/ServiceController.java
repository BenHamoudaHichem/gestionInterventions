package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.services.HomeService;
import com.app.gestionInterventions.services.TNCitiesClient;
import com.app.gestionInterventions.services.password.ChangePasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*",maxAge = 36000)
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    ChangePasswordService changePasswordService;
    @Autowired
    TNCitiesClient tnCitiesClient;
    @Autowired
    HomeService homeService;
    @GetMapping("/homeLoader/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public HomeService.HomeManagerLoader homeManagerLoader()
    {
        return this.homeService.homeManagerLoader();
    }
    @GetMapping("/homeLoader/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public HomeService.HomeCustomerLoader homeCustomerLoader()
    {
        return this.homeService.homeCustomerLoader();
    }
    @GetMapping("/states")
    public List<String> allStates()
    {
        return this.tnCitiesClient.getStates().getData();
    }
    @GetMapping("/states/{state}")
    public List<String> test(@PathVariable(value = "state")String state)
    {
        return this.tnCitiesClient.getCitiesByState(state).getData();
    }
    @PutMapping("password/change")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordService.PasswordRequest passwordRequest) throws ResourceNotFoundException {
        if(this.changePasswordService.doUpdate(passwordRequest)){
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre mot de passe est modifiée avec succes"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST,"Il y a une erreur"));
    }
}
