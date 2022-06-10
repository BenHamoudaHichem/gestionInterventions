package com.app.gestionInterventions.repositories.work.intervention.intervention;

import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InterventionRepositoryCustom extends ICrud<Intervention> {

    Optional<List<Intervention>> all();
    Optional<List<Intervention>>searchOr(Map<String,String> entries, Sort sort);;
    Optional<List<Intervention>>searchAnd(Map<String,String> entries, Sort sort);;
}
