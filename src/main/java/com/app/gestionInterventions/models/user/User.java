package com.app.gestionInterventions.models.user;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.user.role.Role;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document(collection = "users")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})

public class User implements Serializable {
    @Id
    private String id;

    @NotBlank
    @Size(min=2,max = 50)
    private String firstName;

    @NotBlank
    @Size(min=2,max = 50)
    private String lastName;
    @NotBlank
    @Size(min = 8)
    private String identifier;

    @NotNull
    @Size(min = 8,max = 120)
    private String password;

    @NotNull
    private Address address;
    @NotBlank(message = "Téléphone doit etre composé d 8 chiffre")
    @Size(min = 8,max = 8)
    private String tel;

    @DBRef
    private Set<Role> roles = new HashSet<>();
    @JsonCreator
    public User(String id,String firstName, String lastName, String identifier, String password, Address address, String tel) {

        this.id=id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identifier = identifier;
        this.password = password;
        this.address = address;
        this.tel = tel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", identifier='" + identifier + '\'' +
                ", password='" + password + '\'' +
                ", address=" + address +
                ", tel='" + tel + '\'' +
                ", roles=" + roles +
                '}';
    }
}
