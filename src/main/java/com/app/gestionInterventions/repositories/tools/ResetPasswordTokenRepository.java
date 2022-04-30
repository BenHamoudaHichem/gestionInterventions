package com.app.gestionInterventions.repositories.tools;

import com.app.gestionInterventions.models.tools.ResetPasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public class ResetPasswordTokenRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ResetPasswordTokenRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public Optional<ResetPasswordToken> create(ResetPasswordToken resetPasswordToken)
    {
        return Optional.ofNullable(mongoTemplate.save(resetPasswordToken));
    }
    public boolean existsByToken(String token)
    {
        Query query=new Query();
        query.addCriteria(Criteria.where("token").is(token));
        return this.mongoTemplate.exists(query,ResetPasswordToken.class);
    }
    public Optional<ResetPasswordToken> findByToken(String token)
    {
        Query query=new Query();
        query.addCriteria(Criteria.where("token").is(token));
        return Optional.ofNullable(this.mongoTemplate.findOne(query,ResetPasswordToken.class));
    }

}
