package com.app.gestionInterventions.models.work.intervention.category;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Document(collection = "categories")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})
public class Category {
    @Id
    private String id;
    @NotBlank
    @Indexed(unique = true)
    private String name;

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }
    @JsonCreator
    public Category( String name) {
        this.id = null;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
