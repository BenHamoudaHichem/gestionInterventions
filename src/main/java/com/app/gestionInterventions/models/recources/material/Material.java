package com.app.gestionInterventions.models.recources.material;

import com.app.gestionInterventions.models.additional.Address;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Document(collection = "materials")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Material {
    @Id
    private String id;
    @NotBlank
    @Size(min = 3,max = 30)
    private String name;
    @NotBlank
    @Size(min = 3,max = 254)
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Past
    private Date dateOfPurchase;
    @NotNull
    private Address address;
    @NotBlank
    private Status status;
    public Material(String id, String name, String description, Date dateOfPurchase, Address address, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateOfPurchase = dateOfPurchase;
        this.address = address;
        this.status = status;
    }
    @JsonCreator
    public Material(@JsonProperty("name")  String name, @JsonProperty("description")  String description, @JsonProperty("dateOfPurchase")  Date dateOfPurchase, @JsonProperty("address") Address address, @JsonProperty("status") Status status) {
        this.name = name;
        this.description = description;
        this.dateOfPurchase = dateOfPurchase;
        this.address = address;
        this.status = status;
    }
    public Material(){}



    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }
}
