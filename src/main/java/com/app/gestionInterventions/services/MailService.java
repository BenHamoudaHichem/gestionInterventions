
package com.app.gestionInterventions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
public class  MailService {
    @Autowired
    private JavaMailSender emailSender;
    private final String emailIntervent = "gestintervent@gmail.com";
    @Autowired
    private  SpringTemplateEngine templateEngine;


    public void sendContactUsEmail(Email email) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,  MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        HashMap hashMap=new HashMap();
        hashMap.put("name",email.getFullName());
        hashMap.put("tel",email.getPhone());
        hashMap.put("email",email.getEmail());
        hashMap.put("desc",email.getDescription());
        hashMap.put("logo","logo");
        hashMap.put("laptop","laptop");


        context.setVariables(hashMap);

        helper.setSubject("ArabIntervent- Email de contact");
        helper.setTo(emailIntervent);
        final String  html = templateEngine.process("sendContactUsEmail.html", context);
        helper.setText(html,true);
        helper.addInline("logo", new ClassPathResource("images/logo.png"), "image/png");
        helper.addInline("laptop", new ClassPathResource("images/laptop2.jpg"), "image/jpg");
        emailSender.send(message);
    }
    public void resetPassword(Email email) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,  MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        HashMap hashMap=new HashMap();
        hashMap.put("name",email.getFullName());
        hashMap.put("tel",email.getPhone());
        hashMap.put("email",email.getEmail());
        hashMap.put("link",email.getDescription());
        hashMap.put("logo","logo");
        hashMap.put("gif","gif");
        hashMap.put("laptop","laptop");


        context.setVariables(hashMap);

        helper.setSubject("ArabIntervent- Réinitialiser votre mot de passe");
        helper.setTo(email.email);
        final String  html = templateEngine.process("resetPasswordEmail.html", context);
        helper.setText(html,true);
        helper.addInline("logo", new ClassPathResource("images/logo.png"), "image/png");
        helper.addInline("gif", new ClassPathResource("images/link.gif"), "image/gif");
        helper.addInline("laptop", new ClassPathResource("images/laptop.jpg"), "image/jpg");

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
