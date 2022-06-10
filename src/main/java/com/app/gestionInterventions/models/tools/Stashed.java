package com.app.gestionInterventions.models.tools;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document(collection = "stasheds")
public class Stashed<T> {
    @Id
    private String id;
    @NotNull
    private T objectStashed;
    private final LocalDateTime createdAt;
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Stashed(@JsonProperty(value = "id",required = false)String id,
                   @JsonProperty(value = "objectStashed",required = true)T objectStashed,
                   @JsonProperty(value = "createdAt",required = false)LocalDateTime createdAt) {
        this.id = id;
        this.objectStashed = objectStashed;
        this.createdAt= createdAt==null?LocalDateTime.now():createdAt;

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
