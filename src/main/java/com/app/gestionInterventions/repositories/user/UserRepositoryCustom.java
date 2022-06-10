package com.app.gestionInterventions.repositories.user;

import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.repositories.ICrud;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepositoryCustom extends ICrud<User> {
    boolean existsByIdentifier(String identifier);
    Optional<List<User>> all();
    Optional<List<User>> searchAnd(Map<String,String> entries, Sort sort);;
    Optional<List<User>> findByRole(Role role);
    Optional<User> findByIdentifier(String identifier);
    Optional<List<User>>searchOr(Map<String,String> entries, Sort sort);;
}
