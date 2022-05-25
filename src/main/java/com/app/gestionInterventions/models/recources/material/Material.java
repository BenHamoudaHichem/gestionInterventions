package com.app.gestionInterventions.models.recources.material;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.QuantityValue;
import com.fasterxml.jackson.annotation.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Document(collection = "materials")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",} )
public class Material {
    @Id
    protected String id;
    @NotBlank
    @Size(min = 3,max = 30)
    protected String name;
    @NotBlank
    @Size(min = 3,max = 254)
    protected String description;
    @NotNull
    protected QuantityValue totalQuantity;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Past
    protected Date dateOfPurchase;
    @NotNull
    protected Address address;
    @NotNull
    protected ECategory category;
    @NotBlank
    protected Status status;
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Material(@JsonProperty(value = "id",required = false) String id,
                    @JsonProperty(value = "name",required = false)String name,
                    @JsonProperty(value = "description",required = false)String description,
                    @JsonProperty(value = "totalQuantity",required = false)QuantityValue totalQuantity,
                    @JsonProperty(value = "dateOfPurchase",required = false)Date dateOfPurchase,
                    @JsonProperty(value = "address",required = false)Address address,
                    @JsonProperty(value = "category",required = false)ECategory category,
                    @JsonProperty(value = "status",required = false)Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalQuantity=totalQuantity;
        this.dateOfPurchase = dateOfPurchase;
        this.address = address;
        this.category=category;
        this.status = status;
        if (category.equals(ECategory.Material)) {
            this.totalQuantity.setQuantityToUse(0.0f);
        }
    }

    public ECategory getCategory() {
        return category;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QuantityValue getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(QuantityValue totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Material)) {
            return false;
        }
        Material material = (Material) o;
        return getId().equals(material.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
