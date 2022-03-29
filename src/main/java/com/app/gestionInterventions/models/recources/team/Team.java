package com.app.gestionInterventions.models.recources.team;

import com.app.gestionInterventions.models.user.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "teams")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})
public class Team {
    @Id
    private String id;
    @NotBlank
    @Size(min = 2,max = 60)
    private String name;
    @DBRef(lazy = true)
    private User manager;
    @DBRef(lazy = true)
    private List<User> members;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Team(@JsonProperty(value = "id",required = false) String id,
                @JsonProperty(value = "name",required = true) String name,
                @JsonProperty(value = "manager",required = true) User manager,
                @JsonProperty(value = "members",required = true) List<User> members) {
        this.id = id;
        this.name=name;
        this.manager = manager;
        this.members = members;
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

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
