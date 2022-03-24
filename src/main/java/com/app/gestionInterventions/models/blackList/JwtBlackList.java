package com.app.gestionInterventions.models.blackList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "JwtBlackList")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtBlackList {
    @Id
    @JsonIgnore
    private final String token;
    private final LocalDateTime createdAt= LocalDateTime.now();

    public JwtBlackList(String token) {
        this.token = token.substring(7,token.length());
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
