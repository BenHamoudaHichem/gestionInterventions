package com.app.gestionInterventions.models.user.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "roles")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})
public class Role implements Serializable {
    @Id
    private String id;

    private ERole name;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}
