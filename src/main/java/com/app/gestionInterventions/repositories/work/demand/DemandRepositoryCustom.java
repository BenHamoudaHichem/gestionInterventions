package com.app.gestionInterventions.repositories.work.demand;

import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DemandRepositoryCustom extends ICrud<Demand> {
    Optional<List<Demand>>searchOr(Map<String,String> entries, Sort sort);
    Optional<List<Demand>>searchAnd(Map<String,String> entries, Sort sort);
    Optional<List<Demand>>allByUser(String id);
}
