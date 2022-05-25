package com.app.gestionInterventions.models.work.intervention;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.MaterialUsed;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
@Document(collection = "InterventionCloseds")
public class InterventionClosed extends Intervention {
    @NotBlank
    private String closingComment;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date closedDate;
    @NotNull
    private final List<MaterialUsed>materialUsedList;
    @NotNull
    private Team workingGroup;

    @JsonCreator
    public InterventionClosed(
            @JsonProperty(value = "id",required = false) String id,
            @JsonProperty(value = "title",required = true) String title,
            @JsonProperty(value = "description",required = true)String description,
            @JsonProperty(value = "category",required = true)Category category,
            @JsonProperty(value = "address",required = true)Address address,
            @JsonProperty(value = "startedAt",required = true)Date startedAt,
            @JsonProperty(value = "expiredAt",required = true)Date expiredAt,
            @JsonProperty(value = "demandList",required = true)List<Demand> demandList,
            @JsonProperty(value = "materialsToBeUsed",required = true)List<MaterialUsed>  materialsToBeUsed,
            @JsonProperty(value = "team",required = true)Team team,
            @JsonProperty(value = "status",required = true)Status status,
            @JsonProperty(value = "createdAt",required = false)LocalDateTime createdAt,
            @JsonProperty(value = "closingComment",required = true)String closingComment,
            @JsonProperty(value = "closedDate",required = true)Date closedDate,
            @JsonProperty(value = "materialUsedList",required = true)List<MaterialUsed> materialUsedList,
            @JsonProperty(value = "workingGroup",required = true)Team workingGroup
            ) {

        super(id, title, description, category, address, startedAt, expiredAt, demandList, materialsToBeUsed, team, status, createdAt);

        this.closingComment = closingComment;
        this.closedDate =closedDate==null ?Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)):closedDate;
        this.materialUsedList = materialUsedList;
        this.workingGroup=workingGroup;
    }

    public Team getWorkingGroup() {
        return workingGroup;
    }

    public String getClosingComment() {
        return closingComment;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public List<MaterialUsed> getMaterialUsedList() {
        return materialUsedList;
    }
}
