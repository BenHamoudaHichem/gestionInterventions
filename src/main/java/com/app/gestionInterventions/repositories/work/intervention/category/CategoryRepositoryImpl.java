package com.app.gestionInterventions.repositories.work.intervention.category;

import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
    private final MongoTemplate mongoTemplate;


    @Autowired
    public CategoryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Category> create(Category category) {


        this.checkIndex();

        return Optional.of(this.mongoTemplate.save(category));
    }

    @Override
    public long update(String id, Category category) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update =new Update();

        update.set("name",category.getName());

        return this.mongoTemplate.updateFirst(query,update, Category.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return this.mongoTemplate.remove(query,Category.class).getDeletedCount();
    }

    @Override
    public Optional<Category> findByName(String name ) {
        Query query = new Query();
        query.addCriteria(new Criteria("eCategory").is(name));
        return Optional.ofNullable(mongoTemplate.findOne(query,Category.class));
    }

    @Override
    public boolean existsByName(String name) {
        return this.findByName(name).isPresent();
    }

    @Override
    public Optional<List<Category>> all() {
        return Optional.ofNullable(this.mongoTemplate.findAll(Category.class));
    }

    @Override
    public Optional<Category> findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.ofNullable(this.mongoTemplate.findOne(query,Category.class));

    }
    public Optional<List<Category>> search(String key, String value) {


        MatchOperation matchOperation = Aggregation.match(new Criteria(key).regex(value));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<Category> aggregationResults = this.mongoTemplate.aggregate(aggregation,"categories",Category.class);
        return Optional.of(aggregationResults.getMappedResults());
    }
    @Override
    public Optional<List<Intervention>> findInterventionsByCategory(String id) {
        Query query=new Query();
        query.addCriteria(Criteria.where("category.$id").is(new ObjectId(id)));
        return Optional.ofNullable(this.mongoTemplate.find(query,Intervention.class));
    }
    public long countInterventionsByCategory(String id) {
        Query query=new Query();
        query.addCriteria(Criteria.where("category.$id").is(new ObjectId(id)));
        return this.mongoTemplate.count(query,Intervention.class);
    }
    public List<Category> create(List<Category> categoryList) {
        return categoryList.stream().map(category -> {checkIndex();

            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.create(category).orElse(null);
        }).collect(Collectors.toList());
    }

    @Override
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(Category.class);
    }

    private void checkIndex()
    {
        this.mongoTemplate.indexOps(Category.class).ensureIndex(
                new CompoundIndexDefinition(new Document()).on("name", Sort.Direction.ASC).unique()
        );
    }
}
