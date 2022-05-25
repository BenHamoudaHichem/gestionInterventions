package com.app.gestionInterventions.models.tools;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document(collection = "stasheds")
public class Stashed {
    @Id
    private String id;
    @NotNull
    private Object objectStashed;
    private final LocalDateTime createdAt;

    public Stashed(String id, Object objectStashed, LocalDateTime createdAt) {
        this.id = id;
        this.objectStashed = objectStashed;
        this.createdAt= createdAt==null?LocalDateTime.now():createdAt;

    }
    public Stashed(Object objectStashed) {
        this.id = null;
        this.objectStashed = objectStashed;
        this.createdAt= LocalDateTime.now();

    }
    public String getId() {
        return id;
    }

    public Object getObjectStashed() {
        return objectStashed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
