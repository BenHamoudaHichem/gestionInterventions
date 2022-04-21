package com.app.gestionInterventions.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;

public class MailConfig {
    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("Ahmed");
        message.setText(
                "This is the TEST email template for your email:\n%s\n");
        return message;
    }
}
