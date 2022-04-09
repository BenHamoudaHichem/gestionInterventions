package com.app.gestionInterventions.models.work.intervention.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "categories")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})
public class Category {
    @Id
    private String id;
    @NotBlank
    private String name;
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
