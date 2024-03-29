package com.app.gestionInterventions.models.recources.material;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.QuantityValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;

public class MaterialUsed extends Material {

    @NotBlank
    private QuantityValue quantityToUse;
    private LocalDateTime dateOfUse;

    @JsonCreator
    public MaterialUsed(String id, String name, String description, QuantityValue totalQuantity, Date dateOfPurchase, Address address,ECategory category, Status status, QuantityValue quantityToUse, LocalDateTime dateOfUse) {
        super(id, name, description, totalQuantity, dateOfPurchase, address,category, status);
        this.quantityToUse = quantityToUse;
        this.dateOfUse = dateOfUse==null ?LocalDateTime.now():dateOfUse;
    }

    public QuantityValue getQuantityToUse() {
        return quantityToUse;
    }

    public void setQuantityToUse(QuantityValue quantityToUse) {
        this.quantityToUse = quantityToUse;
    }

    public LocalDateTime getDateOfUse() {
        return dateOfUse;
    }

    public void setDateOfUse(LocalDateTime dateOfUse) {
        this.dateOfUse = dateOfUse;
    }



}
