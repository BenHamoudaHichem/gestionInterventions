package com.app.gestionInterventions.payload.request;

import com.app.gestionInterventions.models.additional.Address;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
public class RegisterUserRequest {

    @NotBlank
    @Size(min=2,max = 20)
    private String firstName;

    @NotBlank
    @Size(min=2,max = 20)
    private String lastName;

    @NotBlank
    @Size(max = 10,min = 8)
    private String identifier;

    @NotBlank
    @Size(min = 8,max = 120)
    private String password;

    @NotNull
    private Address address;

    @NotNull
    @Size(min = 8,max = 8)
    private String tel;

    private Set<String> roles;

    @JsonCreator
    public RegisterUserRequest(String firstName, String lastName, String identifier, String password, Address address, String tel, Set<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identifier = identifier;
        this.password = password;
        this.address = address;
        this.tel = tel;
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getAdresse() {
        return address;
    }

    public void setAdresse(Address address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "RegisterUserRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", identifier='" + identifier + '\'' +
                ", password='" + password + '\'' +
                ", address=" + address +
                ", tel='" + tel + '\'' +
                ", roles=" + roles +
                '}';
    }
}
