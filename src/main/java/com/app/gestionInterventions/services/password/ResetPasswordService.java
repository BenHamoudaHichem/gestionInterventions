package com.app.gestionInterventions.services.password;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.tools.ResetPasswordToken;
import com.app.gestionInterventions.repositories.tools.ResetPasswordTokenRepository;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.UUID;
@Service
public class ResetPasswordService {

    private static final String URL="http://localhost:4200/reset-password";

    @Autowired
     MailService mailService;
     AESPasswordEncoder serviceEncoder=new AESPasswordEncoder();
    @Autowired
     ResetPasswordTokenRepository resetPasswordTokenRepository;
    @Autowired
     UserRepositoryImpl userRepository;
    @Autowired
    ChangePasswordService changePasswordService;
    private ResetPasswordToken resetPasswordToken=null;


    public String generateResetPasswordURL(String mail) throws ResourceNotFoundException, MessagingException {
        if (!userRepository.existsByIdentifier(mail)) {
            System.out.println(!userRepository.existsByIdentifier(mail));
            throw new ResourceNotFoundException();
        }
        String token = generateToken();
        this.resetPasswordToken=new ResetPasswordToken(token,userRepository.findByIdentifier(mail).get());
        if(this.resetPasswordTokenRepository.create(this.resetPasswordToken).isPresent())
        {
            this.mailService.resetPassword(new MailService.Email(resetPasswordToken.getUser().getFirstName().concat(" "+resetPasswordToken.getUser().getLastName()),resetPasswordToken.getUser().getTel(),mail,URL.concat("/"+(token))));
            return URL.concat("/"+(token));
        }
        return null;
    }

    public boolean doUpdate(String cryptedtoken,String newPassword)
    {
        String token = (cryptedtoken);
        if (this.validateToken(token)) {
            return this.changePasswordService.doUpdate(resetPasswordTokenRepository.findByToken(token).get().getUser(),newPassword);
        }
        return false;
    }

    private String generateToken()
    {
        return UUID.randomUUID().toString().replace("/","");
    }

    public boolean validateToken(String token)
    {
        return this.resetPasswordTokenRepository.existsByToken(token)
                &&this.resetPasswordTokenRepository.findByToken(token).get().getExpiryDate().compareTo(new Date())>=0;
    }
}
