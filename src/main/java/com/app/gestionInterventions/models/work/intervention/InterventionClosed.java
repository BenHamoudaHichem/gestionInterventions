package com.app.gestionInterventions.models.work.intervention;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.MaterialUsed;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Document(collection = "InterventionCloseds")
public class InterventionClosed extends Intervention {
    private String closingComment;
    private LocalDateTime closedDate;
    private final List<MaterialUsed>materialUsedList;

    public InterventionClosed(String id, String title, String description, Category category, Address address, Date startedAt, Date expiredAt, List<Demand> demandList, List<MaterialUsed> materialsToBeUsed, Team team, Status status, LocalDateTime createdAt, String closingComment, LocalDateTime closedDate, List<MaterialUsed> materialUsedList) {
        super(id, title, description, category, address, startedAt, expiredAt, demandList, materialsToBeUsed, team, status, createdAt);
        this.closingComment = closingComment;
        this.closedDate = closedDate;
        this.materialUsedList = materialUsedList;
    }

    public String getClosingComment() {
        return closingComment;
    }

    public LocalDateTime getClosedDate() {
        return closedDate;
    }

    public List<MaterialUsed> getMaterialUsedList() {
        return materialUsedList;
    }
}
