package com.app.gestionInterventions.models.work.intervention;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "interventions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Intervention {

    @Id
    protected String id;
    @NotBlank
    @Size(min = 8,max = 100)
    protected String title;
    @NotBlank
    @Size(min = 8,max = 255)
    protected String description;
    @NotBlank
    protected Category category;
    @NotNull
    protected Address address;
    @JsonFormat(pattern = "dd-MM-yyyy")
    protected LocalDateTime startedAt;
    @NotBlank
    protected Status status;
    @DBRef(lazy = true)
    protected List<Demand> demandList;
    @DBRef(lazy = true)
    protected Team team;
    protected final LocalDateTime createdAt;



    public Intervention(@JsonProperty(value = "id",required = false) String id,
                        @JsonProperty(value = "title",required = true) String title,
                        @JsonProperty(value = "description",required = true)String description,
                        @JsonProperty(value = "category",required = true)Category category,
                        @JsonProperty(value = "address",required = true)Address address,
                        @JsonProperty(value = "startedAt",required = true)LocalDateTime startedAt,
                        @JsonProperty(value = "demandList",required = true)Status status, List<Demand> demandList,
                        @JsonProperty(value = "team",required = true)Team team,
                        @JsonProperty(value = "createdAt",required = false)LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.address = address;
        this.startedAt = startedAt;
        this.status = status;
        this.demandList = demandList;
        this.team = team;
        this.createdAt = createdAt== null ? LocalDateTime.now():createdAt;
    }

    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Demand> getDemandList() {
        return demandList;
    }

    public void setDemandList(List<Demand> demandList) {
        this.demandList = demandList;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
