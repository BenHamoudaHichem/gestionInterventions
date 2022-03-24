package com.app.gestionInterventions.repositories.resources.team;

import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.repositories.ICrud;

import java.util.List;
import java.util.Optional;

public interface TeamRepositoryCustom extends ICrud<Team> {
    boolean isAvailable();
    Optional<List<Team>> teamAvailable();
    int nbIntervention();
}
