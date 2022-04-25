package com.app.gestionInterventions.services.statistics;

import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.Status;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaterialStatistic {

    @Autowired
    MaterialRepositoryImpl materialRepository;


    public List<PairCustom> pieStatus (){
        List<PairCustom> listResult= new ArrayList<>();

        List<Material> materialList= new ArrayList<>(materialRepository.all().orElse(null));

        Arrays.asList(Status.values()).forEach(status -> {
            listResult.add(new PairCustom(getName(status),0));
        });
        materialList.forEach(material -> listResult.get(Arrays.stream(Status.values()).collect(Collectors.toList()).indexOf(material.getStatus())).increment());
        return listResult;
    }
    private String getName(Status status)
    {
        switch (status) {
            case Functional:
                return "Fonctionnelle";
            case Broken_down:
                return "En_panne";
            case Stoled:
                return"A_volée";
            default:
                return"Hors_service";
        }
    }
}
