package com.app.gestionInterventions.services.statistics;

import com.app.gestionInterventions.models.recources.team.Status;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class TeamStatistic {
    @Autowired
    TeamRepositoryImpl teamRepository;
    public List<PairCustom> pieAvailable(){
        List<PairCustom> listResult= new ArrayList<>();

        listResult.add(new PairCustom("Disponible",this.teamRepository.countTeamByStatus(Status.Available)));
        listResult.add(new PairCustom("Indisponible",this.teamRepository.countTeamByStatus(Status.Unavailable)+this.teamRepository.countTeamByStatus(Status.Out_of_order)));

        return listResult;
    }
}
