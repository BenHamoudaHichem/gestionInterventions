package com.app.gestionInterventions.models.work.demand;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.user.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Document(collection = "demands")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand {
    @Id
    private String id;
    @NotBlank
    @Size(min = 3,max = 50)
    private String title;
    @NotBlank
    @Size(min = 3,max = 255)
    private String description ;

    @NotNull

    private Address address;
    @NotNull
    private Status status;
    @DBRef(lazy = true)
    private User user;
    private final LocalDateTime createdAt;

    public Demand(String id, String title, String description, Address address, Status status, User user, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
    }
    @JsonCreator
    public Demand(@JsonProperty("id")String id, @JsonProperty("title") String title, @JsonProperty("description") String description, @JsonProperty("location") Address address, @JsonProperty("status") Status status, @JsonProperty("user") User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.status = status;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public Demand(String title, String description, Address address, Status status, User user, LocalDateTime createdAt) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.address = address;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Demand(){
        this.createdAt = LocalDateTime.now();
    }


    public Demand(String title, String description, Address address, Status status, User user) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.address = address;
        this.status = status;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Address getAdresse() {
        return address;
    }

    public void setAdresse(Address address) {
        this.address = address;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
