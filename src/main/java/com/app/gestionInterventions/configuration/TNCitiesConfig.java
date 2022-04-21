package com.app.gestionInterventions.configuration;

import com.app.gestionInterventions.services.TNCitiesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration

public class TNCitiesConfig {
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.app.tnlocations.wsdl");
        return marshaller;
    }
    @Bean
    public TNCitiesClient tnCitiesClient( @Autowired Jaxb2Marshaller marshaller) {
        TNCitiesClient client = new TNCitiesClient();
        client.setDefaultUri("http://localhost:8081/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
