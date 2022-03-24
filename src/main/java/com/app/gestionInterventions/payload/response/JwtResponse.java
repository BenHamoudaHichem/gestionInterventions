package com.app.gestionInterventions.payload.response;

import java.util.List;

public class JwtResponse {
    private String token;
    private final String type = "Bearer";
    private String id;
    private String identifier;
    private String username;
    private List<String> roles;

    public JwtResponse(String token,String id, String identifier, String username, List<String> roles) {
        this.token = token;
        this.id = id;
        this.identifier = identifier;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
