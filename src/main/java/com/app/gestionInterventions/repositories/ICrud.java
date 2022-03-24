package com.app.gestionInterventions.repositories;

import java.util.List;
import java.util.Optional;

public interface ICrud<T> {
    Optional<T> create(T t) ;
    long update (String id, T t);
    long detele (String id);
    Optional<List<T>> all() ;
    Optional<T> findById(String id);
    void dropCollection();
}
