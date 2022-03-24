package com.app.gestionInterventions.models.additional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    private BigInteger zipCode;
    @NotBlank
    private String street;
    @NotBlank
    private String city;
    @NotBlank
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String state;
    @NotBlank
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String country;
    @NotNull
    private Location location;

    public Address(BigInteger zipCode, String street, String city, String state, String country, Location location) {
        this.zipCode = zipCode;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.location = location;
    }
    @JsonCreator
    public Address(BigInteger zipCode, String street, String city, String state, String country) {
        this.zipCode = zipCode;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.location = null;
    }

    public BigInteger getZipCode() {
        return zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public String getAddressZone()
    {
        return this.city+" ,"+this.state+" ,"+this.country;
    }
}
