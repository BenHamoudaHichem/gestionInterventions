package com.app.gestionInterventions.repositories.tools;

import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.tools.Stashed;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

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

    public Optional<Stashed>findId(String id)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("user.$id").is(new ObjectId(id)));
        return Optional.of(mongoTemplate.findOne(query,Stashed.class));
    }

}
