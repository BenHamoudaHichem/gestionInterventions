package com.app.gestionInterventions.services.statistics;

import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.work.intervention.category.CategoryRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.intervention.InterventionRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InterventionStatistic {
    @Autowired
    CategoryRepositoryImpl categoryRepository;
    @Autowired
    InterventionRepositoryImpl interventionRepository;
    public List<PairCustom> pieCategory(){
        List<PairCustom> listResult= new ArrayList<>();
        Optional<List<Category>> optionalCategoryList=categoryRepository.all();
        if(optionalCategoryList.isPresent()){
            optionalCategoryList.get().forEach(category -> listResult.add(new PairCustom(category.getName(),categoryRepository.countInterventionsByCategory(category.getId()))));
        }
        return listResult;
    }
}
