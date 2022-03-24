package com.app.gestionInterventions.repositories.resources.team;

import com.app.gestionInterventions.models.recources.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class TeamRepositoryImpl implements TeamRepositoryCustom{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TeamRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Team> create(Team team) {
        return Optional.of(this.mongoTemplate.save(team));
    }

    @Override
    public long update(String id, Team team) {
        return 0;
    }

    @Override
    public long detele(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return this.mongoTemplate.remove(query,Team.class).getDeletedCount();
    }


    @Override
    public Optional<List<Team>> all() {
        return Optional.of(this.mongoTemplate.findAll(Team.class));
    }

    @Override
    public Optional<Team> findById(String id) {
        Query query= new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.of(this.mongoTemplate.findOne(query,Team.class));
    }
    public Optional<List<Team>> search(String key, String value) {

        MatchOperation matchOperation = Aggregation.match(new Criteria(key).regex(value));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<Team> aggregationResults = this.mongoTemplate.aggregate(aggregation,"teams",Team.class);
        return Optional.of(aggregationResults.getMappedResults());

    }
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public Optional<List<Team>> teamAvailable() {
        return Optional.empty();
    }

    @Override
    public int nbIntervention() {
        return 0;
    }
    @Override
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(Team.class);
    }
}
