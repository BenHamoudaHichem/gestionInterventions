package com.app.gestionInterventions.repositories.work.intervention.interventionClosed;

import com.app.gestionInterventions.models.recources.material.ECategory;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.InterventionClosed;

import com.app.gestionInterventions.models.work.intervention.Status;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@Repository
public class InterventionClosedRepositoryImpl implements InterventionClosedRepositoryCustom {
    private final MongoTemplate mongoTemplate;
    private MaterialRepositoryImpl materialRepository;



    @Autowired
    public InterventionClosedRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.materialRepository=new MaterialRepositoryImpl(mongoTemplate);


    }
    @Override
    public Optional<InterventionClosed> create(InterventionClosed interventionClosed) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(interventionClosed.getTeam().getId()));
        Update update =new Update();
        update.set("status", com.app.gestionInterventions.models.recources.team.Status.Available);
        this.mongoTemplate.updateFirst(query,update, Team.class).getModifiedCount();
        query= new Query();
        query.addCriteria(Criteria.where("_id").is(interventionClosed.getId()));
        update =new Update();
        update.set("status", Status.Completed);
        this.mongoTemplate.updateFirst(query,update, Intervention.class).getModifiedCount();

        interventionClosed.setStatus(Status.Completed);

        interventionClosed.getMaterialUsedList().forEach(m->{
            if (interventionClosed.getMaterialsToBeUsed().contains(m)) {
                if (m.getCategory().equals(ECategory.Matter)) {
                    Material material=materialRepository.findById(m.getId()).get();
                    material.getTotalQuantity().setQuantityToUse(
                            m.getTotalQuantity().getQuantityToUse()+(interventionClosed.getMaterialsToBeUsed().stream().filter(x->x.getId().equals(m.getId())).findFirst().get().getQuantityToUse().getQuantityToUse()-m.getQuantityToUse().getQuantityToUse())
                    );
                }
            }

        });


        return Optional.of(mongoTemplate.save(interventionClosed));
    }

    @Override
    public long update(String id, InterventionClosed interventionClosed) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update =new Update();
        update.set("closingComment",interventionClosed.getClosingComment());
        update.set("materialUsedList",interventionClosed.getMaterialUsedList());
        return this.mongoTemplate.updateFirst(query,update, InterventionClosed.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        return 0;
    }

    @Override
    public Optional<List<InterventionClosed>> all() {
        return Optional.ofNullable(mongoTemplate.findAll(InterventionClosed.class));
    }

    @Override
    public Optional<InterventionClosed> findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.of(this.mongoTemplate.findOne(query,InterventionClosed.class));
    }


    @Override
    public void dropCollection() {

        mongoTemplate.dropCollection(InterventionClosed.class);
    }
}
