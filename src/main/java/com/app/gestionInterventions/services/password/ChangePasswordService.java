package com.app.gestionInterventions.services.password;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {
    @Autowired
    UserRepositoryImpl userRepository;
    @Autowired AESPasswordEncoder passwordEncoder;
    private boolean check(PasswordRequest passwordRequest, User user) {
        return passwordEncoder.matches(passwordRequest.oldPassword,user.getPassword());
    }
    public boolean doUpdate(PasswordRequest passwordRequest) throws ResourceNotFoundException {
        User currentUser =this.userRepository.findById(passwordRequest.getId()).orElseThrow(ResourceNotFoundException::new);
        if (!check(passwordRequest,currentUser)) {
            throw new BadCredentialsException("Votre mot de passe est incorrect !");
        }
        currentUser.setPassword(passwordEncoder.encode(passwordRequest.newPassword));
        return userRepository.update(currentUser.getId(),currentUser)>0;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PasswordRequest{
        private String id;
        private String oldPassword;
        private String newPassword;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public PasswordRequest(String id, String oldPassword, String newPassword) {
            this.id = id;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        public String getId() {
            return id;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }
    }
}
