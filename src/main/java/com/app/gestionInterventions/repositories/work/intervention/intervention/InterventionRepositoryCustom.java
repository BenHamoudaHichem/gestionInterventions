package com.app.gestionInterventions.repositories.work.intervention.intervention;

import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface InterventionRepositoryCustom extends ICrud<Intervention> {

    Optional<List<Intervention>> all(int rows);
    Optional<List<Intervention>> all(int rows,boolean crescent, String factory);
    Optional<List<Intervention>>search(String key,String value, Sort sort);
    Optional<List<Intervention>>search(String key,String value);
}
