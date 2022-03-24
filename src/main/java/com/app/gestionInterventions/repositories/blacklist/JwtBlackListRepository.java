package com.app.gestionInterventions.repositories.blacklist;

import com.app.gestionInterventions.models.blackList.JwtBlackList;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JwtBlackListRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public JwtBlackListRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean toTheBlackList(JwtBlackList jwtBlackList)
    {
        return Optional.of(this.mongoTemplate.save(jwtBlackList)).isPresent();
    }
    public boolean isInTheBlackList(String id)
    {
        if (!this.mongoTemplate.collectionExists(JwtBlackList.class))
        {
            return true;
        }
        Query query= new Query();
        query.addCriteria(new Criteria("_id").is(id));
        if (this.mongoTemplate.exists(query, JwtBlackList.class))
        {
            throw new MalformedJwtException("Token invalid");
        }
        return true;
    }
}
