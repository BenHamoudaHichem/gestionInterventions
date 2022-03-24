package com.app.gestionInterventions.repositories.work.intervention.category;

import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.ICrud;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryCustom extends ICrud<Category> {

    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    Optional<List<Intervention>> findInterventionsByCategory(String id);
}
