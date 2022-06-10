package com.app.gestionInterventions.repositories.resources.material;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.recources.material.ECategory;
import com.app.gestionInterventions.models.recources.material.Material;

import com.app.gestionInterventions.models.recources.material.Status;
import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.repositories.tools.StashedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.thymeleaf.standard.expression.SimpleExpression;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class MaterialRepositoryImpl implements MaterialRepositoryCustom{

    private final MongoTemplate mongoTemplate;
    private StashedRepository stashedRepository;

    @Autowired
    public MaterialRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.stashedRepository=new StashedRepository(mongoTemplate);
    }


    @Override
    public Optional<Material> create(Material material)
    {
        return Optional.of(this.mongoTemplate.save(material));
    }
    public Optional<List<Material>> create(List<Material> material)
    {
        return Optional.of(this.mongoTemplate.insertAll(material).stream().collect(Collectors.toList()));
    }

    @Override
    public long update(String id, Material material) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(material.getId()));

        Update update =new Update();

        update.set("name",material.getName());
        update.set("description",material.getDescription());
        update.set("address",material.getAddress());
        update.set("dateOfPurchase",material.getDateOfPurchase());
        update.set("totalQuantity",material.getTotalQuantity());

        update.set("status",material.getStatus());

        return this.mongoTemplate.updateFirst(query,update,Material.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        stashedRepository.create(new Stashed<Material>(null,this.mongoTemplate.findOne(query, Material.class), LocalDateTime.now()));
        return mongoTemplate.remove(Objects.requireNonNull(this.mongoTemplate.findOne(query, Material.class))).getDeletedCount();
    }

    @Override
    public Optional<List<Material>> all() {
        Query query= new Query();
        query.with(Sort.by(Sort.Direction.ASC, "dateOfPurchase"));
        return Optional.ofNullable(this.mongoTemplate.find(query,Material.class));
    }
    public Optional<List<Material>> findByStatus() {
        Query query= new Query();
        query.with(Sort.by(Sort.Direction.ASC, "dateOfPurchase"));
        return Optional.ofNullable(this.mongoTemplate.find(query,Material.class));
    }

    @Override
    public Optional<Material> findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.ofNullable(this.mongoTemplate.findOne(query,Material.class));
    }

    @Override
    public Optional<List<Material>> searchOr(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((x,y)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(a->!a.getName().equals("class")).map(a->a.getName()).collect(Collectors.toList()).contains(x)){
                    criteriaList.add(Criteria.where("address."+x).regex(y));
                }
                else{
                    criteriaList.add(Criteria.where(x).regex(y));
                }
            } catch (IntrospectionException e) {
                return;
            }
        });
        SortOperation sortOperation = new SortOperation(sort);
        MatchOperation matchOperation = Aggregation.match(new Criteria().orOperator(criteriaList));
        Aggregation aggregation = newAggregation(matchOperation,sortOperation);
        AggregationResults<Material> aggregationResults = this.mongoTemplate.aggregate(aggregation,"materials",Material.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Material>> searchAnd(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((x,y)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(a->!a.getName().equals("class")).map(a->a.getName()).collect(Collectors.toList()).contains(x)){
                    criteriaList.add(Criteria.where("address."+x).regex(y));
                }
                else{
                    criteriaList.add(Criteria.where(x).regex(y));
                }
            } catch (IntrospectionException e) {
                return;
            }
        });
        SortOperation sortOperation = new SortOperation(sort);
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList));
        Aggregation aggregation = newAggregation(matchOperation,sortOperation);
        AggregationResults<Material> aggregationResults = this.mongoTemplate.aggregate(aggregation,"materials",Material.class);
        return Optional.of(aggregationResults.getMappedResults());
    }
    @Override
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(Material.class);
    }

    public long countMaterialByStatus(Status status)
    {
        Query query= new Query();
        query.addCriteria(Criteria.where("status").is(status.name()));
        return this.mongoTemplate.count(query, Material.class);
    }
    public List<Material>availableMaterials() throws ResourceNotFoundException {
        if (!mongoTemplate.collectionExists(Intervention.class)) {
            return this.all().get();
        }
        return this.mongoTemplate.findAll(Material.class).stream().filter(x->!(
              mongoTemplate.findAll(Intervention.class).stream().filter(a->a.getStatus().equals(com.app.gestionInterventions.models.work.intervention.Status.In_Progress)).flatMap(a->a.getMaterialsToBeUsed().stream().filter(b->b.getCategory().equals(ECategory.Material))).collect(Collectors.toList())
            .contains(x))&&!(x.getStatus().equals(Status.Broken_down)||x.getStatus().equals(Status.Expired)||x.getStatus().equals(Status.Stoled))).collect(Collectors.toList());

    }
}
