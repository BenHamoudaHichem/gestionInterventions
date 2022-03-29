package com.app.gestionInterventions.models.additional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    @NotBlank
    private BigDecimal longitude;
    @NotBlank
    private BigDecimal latitude;

    public Location(BigDecimal longitude, BigDecimal latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public Location(Double longitude, Double latitude) {
        this.longitude = BigDecimal.valueOf(longitude);
        this.latitude = BigDecimal.valueOf(latitude);
    }
    public Location(Long longitude,Long latitude) {
        this.longitude = BigDecimal.valueOf(longitude);
        this.latitude = BigDecimal.valueOf(latitude);
    }

    public Location(String longitude, String latitude) {
        this.longitude = new BigDecimal(longitude);
        this.latitude = new BigDecimal(latitude);
    }

    @Override
    public String toString() {
        return "Location{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
    public Location(){}

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}
