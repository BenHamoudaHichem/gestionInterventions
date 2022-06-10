package com.app.gestionInterventions.repositories.work.demand;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.demand.Status;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.tools.StashedRepository;
import com.app.gestionInterventions.services.statistics.DemandStatistic;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class DemandRepositoryImpl implements DemandRepositoryCustom{

    private final MongoTemplate mongoTemplate;
    private StashedRepository stashedRepository;


    @Autowired
    public DemandRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.stashedRepository=new StashedRepository(mongoTemplate);

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
        stashedRepository.create(new Stashed<Demand>(null,this.mongoTemplate.findOne(query, Demand.class), LocalDateTime.now()));

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
    public Optional<List<Demand>> searchOr(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();

        entries.forEach((key,value)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("address."+key).regex(value));
                    return;
                }
            } catch (IntrospectionException e) {
                return;
            }
        });
        SortOperation sortOperation = new SortOperation(sort);
        MatchOperation matchOperation = Aggregation.match(new Criteria().orOperator(criteriaList));

        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<Demand> aggregationResults = this.mongoTemplate.aggregate(aggregation,"demands",Demand.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Demand>> searchAnd(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();

        entries.forEach((key,value)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("address."+key).regex(value));
                    return;
                }
            } catch (IntrospectionException e) {
                return;
            }
            criteriaList.add(new Criteria(key).regex(value));

        });
        SortOperation sortOperation = new SortOperation(sort);
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList));

        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<Demand> aggregationResults = this.mongoTemplate.aggregate(aggregation,"demands",Demand.class);
        return Optional.of(aggregationResults.getMappedResults());
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
    public List<DemandStatistic.DemandPerYear> findStatsYear()
    {
        ProjectionOperation projectionOperation=project("sum").and("month").previousOperation();
        GroupOperation groupOperation= group("month").count().as("sum");
        Aggregation agg = newAggregation(project().andExpression("month(createdAt)").as("month"),
                groupOperation,projectionOperation);

        AggregationResults<DemandStatistic.DemandPerYear> result =
                mongoTemplate.aggregate(agg, "demands", DemandStatistic.DemandPerYear.class);
        return result.getMappedResults();
    }
    private boolean collectionIsEmpty(){return this.mongoTemplate.collectionExists(Demand.class);}


}
