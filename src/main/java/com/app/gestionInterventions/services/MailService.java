
package com.app.gestionInterventions.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
@Component
public class  MailService {
    @Autowired
    private JavaMailSender emailSender;
    private final String emailIntervent = "gestintervent@gmail.com";


   public void sendContactUsEmail(Email email) throws MessagingException {

    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setSubject("Email from arabIntervent");
    helper.setTo(emailIntervent);
    helper.setText(
        "<html> <head> </head><body><style type='text/css'><style> #title{color:red;}</style><br><Strong><i>Bonjour</i></Strong><br><p id='title'>Cette email est envoyé à partir de serveur</p>" + "<hr>"+
        "<br><Strong>Nom de l'expediteur:</Strong><br>"+email.getFullName()+
        "<br><Strong>Numero de telephone:</Strong><br>"+email.getPhone()+
        "<br><Strong>Email:</Strong><br>"+email.getEmail()+
        "<br><Strong>Description</Strong>:<br>"+email.getDescription()+
      " <body></html>",true);

    emailSender.send(message);
}
    public void resetPassword(Email email) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject("ArabIntervent- Reset passwword");
        helper.setTo(email.email);
        helper.setText(
                "<html> <head> </head><body><style type='text/css'><style> #title{color:red;}</style><br><Strong><i>Bonjour</i></Strong><br><p id='title'>Cette email est envoyé à partir de serveur</p>" + "<hr>"+
                        "<br><Strong>Nom de l'expediteur:</Strong><br>"+email.getFullName()+
                        "<br><Strong>Numero de telephone:</Strong><br>"+email.getPhone()+
                        "<br><Strong>Email:</Strong><br>"+email.getEmail()+
                        "<br><Strong>Description</Strong>:<br>"+email.getDescription()+
                        " <body></html>",true);

        emailSender.send(message);
    }

    public static class Email {
        private String fullName;
        private String phone;
        private String email;
        private String description;

        public Email(String fullName, String phone, String email, String description) {
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.description = description;
        }

        public Email() {
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
