package com.app.gestionInterventions.repositories.resources.material;

import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.repositories.ICrud;

import java.util.List;
import java.util.Optional;

public interface MaterialRepositoryCustom extends ICrud<Material> {
    Optional<List<Material>> all(int rows);
    Optional<List<Material>> all(int rows,boolean crescent, String factory);
    Optional<List<Material>>search(String key,String value,boolean crescent, String factory);
    Optional<List<Material>>search(String key,String value);
}
