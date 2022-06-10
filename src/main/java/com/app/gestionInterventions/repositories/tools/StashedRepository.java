package com.app.gestionInterventions.repositories.tools;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.work.demand.Demand;
import org.apache.catalina.util.Introspection;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Repository
public class StashedRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public StashedRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Optional<Stashed>create(Stashed stashed)
    {
        return Optional.of(mongoTemplate.save(stashed));
    }
    public Optional<List<Stashed>>all()
    {
        return Optional.of(mongoTemplate.findAll(Stashed.class));
    }
    public <T extends Object> Optional<List<T>>allByClass(Class<T> c)
    {
        return Optional.of(all().orElse(new ArrayList<>()).stream().filter(x ->x.getObjectStashed().getClass().equals(c))
                        .map(x->(T)x.getObjectStashed())
        .collect(Collectors.toList()));
    }
    public  Optional<List<Stashed>>allStashedByClass(Class<?> c)
    {

        return Optional.of(mongoTemplate.findAll(Stashed.class).stream().filter(stashed ->stashed.getObjectStashed().getClass().equals(c)).collect(Collectors.toList()));
    }


    public Optional<Stashed>findId(String id)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        return Optional.of(mongoTemplate.findOne(query,Stashed.class));
    }
    public <T extends Object> Optional<T> restore(String id) throws ResourceNotFoundException {
        Stashed stashed= findId(id).orElseThrow(ResourceNotFoundException::new);
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(stashed.getId()));
        mongoTemplate.remove(query,Stashed.class);
        T objectRestored = (T) stashed.getObjectStashed();
        return Optional.ofNullable(mongoTemplate.insert(objectRestored));
    }

}
