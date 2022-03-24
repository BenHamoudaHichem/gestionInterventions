package com.app.gestionInterventions.repositories.user;

import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<User> create(User user) {
        return Optional.of(this.mongoTemplate.save(user,"users"));
    }

    @Override
    public long update(String id, User user) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update =new Update();

        update.set("firstName",user.getFirstName());
        update.set("lastName",user.getLastName());
        update.set("address",user.getAddress());
        update.set("password",user.getPassword());
        update.set("role",user.getRoles());
        return this.mongoTemplate.updateFirst(query,update, User.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        return 0;
    }


    @Override
    public Optional<List<User>> all() {
        return Optional.of(this.mongoTemplate.findAll(User.class));
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<List<User>> search(String key, String value) {
        MatchOperation matchOperation = Aggregation.match(new Criteria(key).regex(value));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<User> aggregationResults = this.mongoTemplate.aggregate(aggregation,"users",User.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<User>> findByRole(Role role) {
        MatchOperation matchOperation = Aggregation.match(new Criteria("roles").all(role));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<User> aggregationResults = this.mongoTemplate.aggregate(aggregation,"users",User.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<User> findByIdentifier(String identifier) {
        return Optional.of(this.search("identifier",identifier).get().get(0));
    }

    @Override
    public boolean existsByIdentifier(String identifier){
        Query query= new Query();
        query.addCriteria(Criteria.where("identifier").is(identifier));
        return this.mongoTemplate.exists(query,User.class);
    }
    public boolean create(List<User> userList)
    {

        return this.mongoTemplate.insertAll(userList).size()>0;
    }

    @Override
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(User.class);
    }
}
