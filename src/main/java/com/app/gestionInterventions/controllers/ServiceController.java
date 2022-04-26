package com.app.gestionInterventions.controllers;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;

import com.app.gestionInterventions.payload.response.MessageResponse;
import com.app.gestionInterventions.services.HomeService;
import com.app.gestionInterventions.services.MailService;
import com.app.gestionInterventions.services.TNCitiesClient;
import com.app.gestionInterventions.services.password.ChangePasswordService;
import com.app.gestionInterventions.services.statistics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
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
    @Autowired
    DemandStatistic demandStatistic;
    @Autowired
    MaterialStatistic materialStatistic;
    @Autowired
    TeamStatistic teamStatistic;
    @Autowired
    InterventionStatistic interventionStatistic;
    @Autowired
    MailService mailService;
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
    public List<String> cities(@PathVariable(value = "state")String state)
    {
        return this.tnCitiesClient.getCitiesByState(state).getData();
    }

    @PutMapping("/password/change")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordService.PasswordRequest passwordRequest) throws ResourceNotFoundException {
        if(this.changePasswordService.doUpdate(passwordRequest)){
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"Votre mot de passe est modifiée avec succes"));
        }
        return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST,"Il y a une erreur"));
    }

    @GetMapping("/stats/demands")
    public List<DemandStatistic.DemandPerYear>  demandPerYears()
    {
        return this.demandStatistic.getDemandPerYearList();
    }
    @GetMapping("/stats/materials/pie")
    public List<PairCustom> pieMaterials()
    {
        return this.materialStatistic.pieStatus();
    }
    @GetMapping("/stats/teams/pie")
    public List<PairCustom>pieTeams()
    {
        return this.teamStatistic.pieAvailable();
    }
    @GetMapping("/stats/categories/pie")
    public List<PairCustom> pieCategories()
    {
        return this.interventionStatistic.pieCategory();
    }
    @PostMapping("/contact")
    public  ResponseEntity<MessageResponse> contact(@RequestBody MailService.Email email)  {
        try {
            this.mailService.send(email);
            return ResponseEntity.ok(new MessageResponse(HttpStatus.CREATED,"message envoyé avec succés"));
        }
        catch (MessagingException e) {
            return  ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST,"erreur d'envoi un mail : "+e.getMessage()));
        }
    }

}
