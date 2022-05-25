package com.app.gestionInterventions.repositories.work.demand;

import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface DemandRepositoryCustom extends ICrud<Demand> {
    Optional<List<Demand>> all(int rows);
    Optional<List<Demand>> all(int rows,boolean crescent, String factory);
    Optional<List<Demand>>search(String key,String value, Sort sort);
    Optional<List<Demand>>search(String key,String value);
    Optional<List<Demand>>allByUser(String id);
}
