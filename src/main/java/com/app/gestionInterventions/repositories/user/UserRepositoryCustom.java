package com.app.gestionInterventions.repositories.user;

import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom extends ICrud<User> {
    boolean existsByIdentifier(String identifier);
    Optional<List<User>> all();
    Optional<List<User>> search(String key,String value);
    Optional<List<User>> findByRole(Role role);
    Optional<User> findByIdentifier(String identifier);
    Optional<List<User>>search(String key, String value, Sort sort);
}
