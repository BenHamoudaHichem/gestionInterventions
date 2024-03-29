package com.app.gestionInterventions.models.work.demand;


import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.user.User;
import com.fasterxml.jackson.annotation.*;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Document(collection = "demands")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})
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

    @DBRef
    private User user;

    private final LocalDateTime createdAt;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Demand(@JsonProperty(value = "id",required = false)String id,
                  @JsonProperty(value = "title",required = false)String title,
                  @JsonProperty(value = "description",required = false)String description,
                  @JsonProperty(value = "address",required = false)Address address,
                  @JsonProperty(value = "status",required = false)Status status,
                  @JsonProperty(value = "user",required = false)User user,
                  @JsonProperty(value = "createdAt",required = false)LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt== null ? LocalDateTime.now():createdAt;
    }

    public String getId() {
        return id;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
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
