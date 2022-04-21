package com.app.gestionInterventions.repositories.work.demand;

import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.demand.Status;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class DemandRepositoryImpl implements DemandRepositoryCustom{

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DemandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Demand> create(Demand demand)
    {
        return Optional.of(this.mongoTemplate.save(demand,"demands"));
    }

    @Override
    public long update(String id, Demand demand) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update =new Update();

        update.set("title",demand.getTitle());
        update.set("description",demand.getDescription());
        update.set("address",demand.getAddress());
        update.set("status",demand.getStatus());
        return this.mongoTemplate.updateFirst(query,update,Demand.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.remove(Objects.requireNonNull(this.mongoTemplate.findOne(query, Demand.class))).getDeletedCount();
    }

    @Override
    public Optional<List<Demand>> all() {

        return Optional.of(this.mongoTemplate.findAll(Demand.class));
    }

    @Override
    public Optional<Demand> findById(String id) {
        Query query =new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.of(this.mongoTemplate.findOne(query,Demand.class));
    }

    @Override
    public Optional<List<Demand>> all(int rows) {
        Query query = new Query();
        query.limit(rows);
        return Optional.of(this.mongoTemplate.find(query,Demand.class));
    }

    @Override
    public Optional<List<Demand>> all(int rows, boolean crescent, String factory) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(!crescent)
        {
            direction = Sort.Direction.DESC;
        }
        SortOperation sortOperation = new SortOperation(Sort.by(direction, factory));

        LimitOperation limitOperation = new LimitOperation(Long.parseLong(Integer.toString(rows)));
        TypedAggregation<Demand> typedAggregation = newAggregation(Demand.class,sortOperation,limitOperation);
        AggregationResults<Demand> aggregationResults = this.mongoTemplate.aggregate(typedAggregation,Demand.class);

        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Demand>> search(String key, String value, boolean crescent, String factory) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(!crescent)
        {
            direction = Sort.Direction.DESC;
        }
        SortOperation sortOperation = Aggregation.sort(Sort.by(direction, factory));
        MatchOperation matchOperation =Aggregation.match(Criteria.where(key).regex(value));;


        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<Demand> aggregationResults = this.mongoTemplate.aggregate(aggregation,"demands",Demand.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Demand>> search(String key, String value) {

        return this.search(key,value,false,"createdAt") ;
    }

    @Override
    public Optional<List<Demand>> allByUser(String id) {
        Query query=new Query();
        query.addCriteria(Criteria.where("user.$id").is(new ObjectId(id)));
        return Optional.of(this.mongoTemplate.find(query,Demand.class));
    }

    public int create(List<Demand> demandList)
    {
        return this.mongoTemplate.insertAll(demandList).size();
    }
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(Demand.class);
    }


    public long CountDemandsByStatus(Status status)
    {
        Query query= new Query();
        if (status == null) {
            return this.mongoTemplate.count(query,Demand.class);
        }
        query.addCriteria(Criteria.where("status").is(status.name()));
        return this.mongoTemplate.count(query,Demand.class);
    }
    public long countUserDemandsByStatus(String id,Status status)
    {
        Query query= new Query();

        query.addCriteria(Criteria.where("status").is(status.name()));
        query.addCriteria(Criteria.where("user.$id").is(new ObjectId(id)));
        return this.mongoTemplate.count(query,Demand.class);
    }

    public List<Demand> findDemandsByStatus(Status status)
    {
        Query query= new Query();
        query.addCriteria(Criteria.where("status").is(status));
        return this.mongoTemplate.find(query,Demand.class);
    }

    private boolean collectionIsEmpty(){return this.mongoTemplate.collectionExists(Demand.class);}



}
