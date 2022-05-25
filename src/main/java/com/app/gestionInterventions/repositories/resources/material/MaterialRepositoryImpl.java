package com.app.gestionInterventions.repositories.resources.material;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Override
    public long update(String id, Material material) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update =new Update();

        update.set("name",material.getName());
        update.set("description",material.getDescription());
        update.set("address",material.getAddress());
        update.set("dateOfPurchase",material.getDateOfPurchase());

        update.set("status",material.getStatus());
        return this.mongoTemplate.updateFirst(query,update,Material.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        stashedRepository.create(new Stashed(this.mongoTemplate.findOne(query, Material.class)));
        return mongoTemplate.remove(Objects.requireNonNull(this.mongoTemplate.findOne(query, Material.class))).getDeletedCount();
    }

    @Override
    public Optional<List<Material>> all() {
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
    public Optional<List<Material>> all(int rows) {
        Query query = new Query();
        query.limit(rows);
        return Optional.of(this.mongoTemplate.find(query,Material.class));
    }

    @Override
    public Optional<List<Material>> all(int rows, boolean crescent, String factory) {
        Sort.Direction direction = Sort.Direction.ASC;
        if(!crescent)
        {
            direction = Sort.Direction.DESC;
        }
        SortOperation sortOperation = new SortOperation(Sort.by(direction, factory));

        LimitOperation limitOperation = new LimitOperation(Long.parseLong(Integer.toString(rows)));
        TypedAggregation<Material> typedAggregation = newAggregation(Material.class,sortOperation,limitOperation);
        AggregationResults<Material> aggregationResults = this.mongoTemplate.aggregate(typedAggregation,Material.class);

        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Material>> search(String key, String value, boolean crescent, String factory) {
        if(key.equals("status")&&value.equals("Available"))
        {
            try {
                return Optional.of(this.availableMaterials());
            } catch (ResourceNotFoundException e) {
                return Optional.ofNullable(null);
            }
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if(!crescent)
        {
            direction = Sort.Direction.DESC;
        }
        SortOperation sortOperation = Aggregation.sort(Sort.by(direction, factory));

        MatchOperation matchOperation = Aggregation.match(new Criteria(key).regex(value));
        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<Material> aggregationResults = this.mongoTemplate.aggregate(aggregation,"materials",Material.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Material>> search(String key, String value) {

        return this.search(key,value,true,"dateOfPurchase") ;
    }
    public int create(List<Material> materialList)
    {
        return this.mongoTemplate.insertAll(materialList).size();
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
        return this.all().orElseThrow(ResourceNotFoundException::new).stream().filter(x->!(
                 mongoTemplate.findAll(Intervention.class).stream().flatMap(y-> y.getMaterialsToBeUsed().stream()).collect(Collectors.toList())
                 .contains(x))&&x.getStatus().equals(Status.Functional)).collect(Collectors.toList());
    }
}
