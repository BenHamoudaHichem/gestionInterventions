package com.app.gestionInterventions.models.additional;

import com.app.gestionInterventions.models.additional.EMeasure;
import com.mongodb.lang.Nullable;

import javax.validation.constraints.NotBlank;

public class QuantityValue {
    @NotBlank
    private Float quantityToUse;
    @Nullable
    private EMeasure measure;

    public QuantityValue(Float quantityToUse, @Nullable EMeasure measure) {
        this.quantityToUse = quantityToUse;
        this.measure = measure;
    }

    public Float getQuantityToUse() {
        return quantityToUse;
    }

    @Nullable
    public EMeasure getMeasure() {
        return measure;
    }

    public void setQuantityToUse(Float quantityToUse) {
        this.quantityToUse = quantityToUse;
    }

    public void setMeasure(@Nullable EMeasure measure) {
        this.measure = measure;
    }
}
