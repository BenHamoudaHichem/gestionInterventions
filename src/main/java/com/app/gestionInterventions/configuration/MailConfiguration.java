package com.app.gestionInterventions.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

import java.math.BigInteger;
@Configuration
public class MailConfiguration {
    @Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("Ahmed");
        message.setText(
                "This is the TEST email template gtgtrbrbggtbfgbgnhnhn bggbgv your email:\n%s\n");
        BigInteger bigInteger;
        BigInteger bigInteger2;
        return message;
    }

}