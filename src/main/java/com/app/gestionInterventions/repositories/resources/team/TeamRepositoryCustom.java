package com.app.gestionInterventions.repositories.resources.team;

import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TeamRepositoryCustom extends ICrud<Team> {
    Optional<List<Team>>searchOr(Map<String,String> entries, Sort sort);
    Optional<List<Team>>searchAnd(Map<String,String> entries, Sort sort);
    boolean isAvailable(Team team);
    Optional<List<Team>> teamAvailable();
    long nbIntervention(Team team);

}
