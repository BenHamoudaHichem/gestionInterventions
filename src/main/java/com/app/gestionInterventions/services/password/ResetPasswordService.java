package com.app.gestionInterventions.services.password;
import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.tools.ResetPasswordToken;
import com.app.gestionInterventions.repositories.tools.ResetPasswordTokenRepository;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSessionListener;
import java.util.Date;
import java.util.UUID;
@Service
public class ResetPasswordService {

    private static final String URL="http://localhost:4200/forget-password";

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
        this.resetPasswordToken=new ResetPasswordToken(token,userRepository.findByIdentifier("11223344").get());
        if(this.resetPasswordTokenRepository.create(this.resetPasswordToken).isPresent())
        {
            this.mailService.sendSimpleMail(new MailService.Email("Abd Rahmen","45210210","hichembenhamouda11@gmail.com",URL.concat("/"+this.serviceEncoder.encode(token))));
            return URL.concat("/"+this.serviceEncoder.encode(token));
        }
        return null;
    }

    public boolean doUpdate(String cryptedtoken,String newPassword)
    {
        String token = serviceEncoder.decrypt(cryptedtoken);
        System.out.println("token");
        if (this.validateToken(token)) {
            System.out.println("token validated");
            return this.changePasswordService.doUpdate(resetPasswordTokenRepository.findByToken(token).get().getUser(),newPassword);
        }
        return false;
    }


    private String generateToken()
    {
        return UUID.randomUUID().toString();
    }
    private boolean validateToken(String token)
    {
        return this.resetPasswordTokenRepository.existsByToken(token)
                &&this.resetPasswordTokenRepository.findByToken(token).get().getExpiryDate().compareTo(new Date())>=0;
    }
}