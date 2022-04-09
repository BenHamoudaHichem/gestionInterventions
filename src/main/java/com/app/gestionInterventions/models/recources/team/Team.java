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
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "teams")
@JsonIgnoreProperties(ignoreUnknown = true,value ={"target","source",})
public class Team {
    @Id
    private String id;
    @NotBlank
    @Size(min = 2,max = 60)
    private String name;
    @DBRef
    private User manager;
    @DBRef
    private List<User> members;
    private Status status;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Team(@JsonProperty(value = "id",required = false) String id,
                @JsonProperty(value = "name",required = false) String name,
                @JsonProperty(value = "manager",required = false) User manager,
                @JsonProperty(value = "members",required = false) List<User> members,
                @JsonProperty(value = "status",required = false)Status status) {
        this.id = id;
        this.name=name;
        this.manager = manager;
        this.members = members;
        this.status=status== null ? Status.Available:status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
    @Override
    public String toString() {
        return "Team{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", manager=" + manager +
                ", members=" + members +
                ", status=" + status +
                '}';
    }

}
