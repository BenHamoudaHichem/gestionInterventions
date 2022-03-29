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
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",} )
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
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Material(@JsonProperty(value = "id",required = false) String id,
                    @JsonProperty(value = "name",required = true)String name,
                    @JsonProperty(value = "description",required = true)String description,
                    @JsonProperty(value = "dateOfPurchase",required = true)Date dateOfPurchase,
                    @JsonProperty(value = "address",required = true)Address address,
                    @JsonProperty(value = "status",required = true)Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateOfPurchase = dateOfPurchase;
        this.address = address;
        this.status = status;
    }




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
