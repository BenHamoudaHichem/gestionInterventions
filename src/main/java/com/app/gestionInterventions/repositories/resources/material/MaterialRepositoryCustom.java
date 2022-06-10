package com.app.gestionInterventions.repositories.resources.material;

import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MaterialRepositoryCustom extends ICrud<Material> {

    Optional<List<Material>>searchOr(Map<String,String> entries, Sort sort);
    Optional<List<Material>>searchAnd(Map<String,String> entries, Sort sort);;
}
