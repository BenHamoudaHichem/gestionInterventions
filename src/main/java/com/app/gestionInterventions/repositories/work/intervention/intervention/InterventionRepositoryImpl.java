package com.app.gestionInterventions.repositories.work.intervention.intervention;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.recources.material.ECategory;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.MaterialUsed;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.Status;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;
import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import com.app.gestionInterventions.repositories.tools.StashedRepository;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.mongodb.DBRef;
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class InterventionRepositoryImpl implements InterventionRepositoryCustom{
    private final MongoTemplate mongoTemplate;
    private TeamRepositoryImpl teamRepository;
    private DemandRepositoryImpl demandRepository;
    private MaterialRepositoryImpl materialRepository;
    private StashedRepository stashedRepository;


    @Autowired
    public InterventionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        teamRepository=new TeamRepositoryImpl(mongoTemplate);
        demandRepository=new DemandRepositoryImpl(mongoTemplate);
        materialRepository= new MaterialRepositoryImpl(mongoTemplate);
        this.stashedRepository=new StashedRepository(mongoTemplate);

    }


    @Override
    public Optional<Intervention> create(Intervention intervention)
    {
        this.checkIndex();
        Query query= new Query();
        query.addCriteria(new Criteria("_id").is(intervention.getTeam().getId()));

        Team team=this.mongoTemplate.findOne(query,Team.class);
        team.setStatus(com.app.gestionInterventions.models.recources.team.Status.Unavailable);
        teamRepository.update(team.getId(),team);
        ArrayList<Demand> demands=new ArrayList<Demand>(intervention.getDemandList());
        demands.forEach(e-> {
            Demand demand=this.demandRepository.findById(e.getId()).get();
            demand.setStatus(com.app.gestionInterventions.models.work.demand.Status.Accepted);
            this.demandRepository.update(demand.getId(),demand);
        });
        intervention.getMaterialsToBeUsed().forEach(i->{
            if (i.getCategory().equals(ECategory.Matter))
            {
                Material material= materialRepository.findById(i.getId()).get();
                float calcul=material.getTotalQuantity().getQuantityToUse()-i.getQuantityToUse().getQuantityToUse();

                material.getTotalQuantity().setQuantityToUse(calcul);
                i.getTotalQuantity().setQuantityToUse(calcul);

                if (material.getTotalQuantity().getQuantityToUse()<=0.0f) {
                    material.setStatus(com.app.gestionInterventions.models.recources.material.Status.Expired);
                }
                materialRepository.update(material.getId(),material);

            }
        });
        return Optional.of(this.mongoTemplate.save(intervention));
    }

    @Override
    public long update(String id, Intervention intervention) {
        Intervention oldIntervention= findById(id).get();
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update =new Update();
        update.set("title",intervention.getTitle());
        update.set("description",intervention.getDescription());
        update.set("expiredAt",intervention.getExpiredAt());
        update.set("demandList",intervention.getDemandList().stream().map(x->new DBRef("demands",new ObjectId(x.getId()))).collect(Collectors.toList()));
        intervention.getMaterialsToBeUsed().forEach(i->{
            if (i.getCategory().equals(ECategory.Matter)&&oldIntervention.getMaterialsToBeUsed().contains(i)) {
                Material material= materialRepository.findById(i.getId()).get();
                float calcul=material.getTotalQuantity().getQuantityToUse()+(i.getQuantityToUse().getQuantityToUse()-oldIntervention.getMaterialsToBeUsed().stream().filter(x->x.equals(i)).findFirst().get().getQuantityToUse().getQuantityToUse());
                material.getTotalQuantity().setQuantityToUse(calcul);
                i.getTotalQuantity().setQuantityToUse(calcul);
                if (material.getCategory().equals(ECategory.Matter)&&material.getTotalQuantity().getQuantityToUse()<=0.0f) {
                    material.setStatus(com.app.gestionInterventions.models.recources.material.Status.Expired);
                }

                materialRepository.update(material.getId(),material);
            }

        });
        update.set("materialsToBeUsed",intervention.getMaterialsToBeUsed());//.stream().map(x->new DBRef("materials",new ObjectId(x.getId()))).collect(Collectors.toList()));

        update.set("status",intervention.getStatus());
        return this.mongoTemplate.updateFirst(query,update,Intervention.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        stashedRepository.create(new Stashed(null,this.mongoTemplate.findOne(query, Intervention.class), LocalDateTime.now()));

        return mongoTemplate.remove(Objects.requireNonNull(this.mongoTemplate.findOne(query, Intervention.class))).getDeletedCount();
    }

    @Override
    public Optional<List<Intervention>> all() {

        return Optional.of(this.mongoTemplate.findAll(Intervention.class));
    }

    @Override
    public Optional<List<Intervention>> searchOr(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((key,value)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("address."+key).regex(value));
                    System.out.println("1");
                }
            } catch (IntrospectionException e) {
                criteriaList.add(new Criteria(key).regex(value));
            }
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Team.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("team."+key).regex(value));
                }
            } catch (IntrospectionException e) {
                criteriaList.add(new Criteria(key).regex(value));
            }
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Material.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    HashMap map = new HashMap();
                    map.put(key,value);
                    criteriaList.add(new Criteria("materialsToBeUsed").all(materialRepository.searchAnd(map,Sort.by(Sort.Direction.DESC,"dateOfPurchase")).orElse(new ArrayList<>())));
                }
            } catch (IntrospectionException e) {
                criteriaList.add(new Criteria(key).regex(value));
            }
        });
        MatchOperation matchOperation = Aggregation.match(new Criteria().orOperator(criteriaList));
        SortOperation sortOperation = new SortOperation(sort);

        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<Intervention> aggregationResults = this.mongoTemplate.aggregate(aggregation,"interventions",Intervention.class);
        return Optional.of(aggregationResults.getMappedResults());

    }

    @Override
    public Optional<List<Intervention>> searchAnd(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((key,value)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("address."+key).regex(value));return;
                }
            } catch (IntrospectionException e) {
                return;
            }
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Team.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class")&&!x.getName().equals("status"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("team."+key).regex(value));return;
                }
            } catch (IntrospectionException e) {
                return;
            }
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Material.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class")&&!x.getName().equals("status"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    HashMap map = new HashMap();
                    map.put(key,value);
                    criteriaList.add(new Criteria("materialsToBeUsed").all(materialRepository.searchOr(map,Sort.by(Sort.Direction.DESC,"dateOfPurchase")).orElse(new ArrayList<>())));
                    return;
                }
            } catch (IntrospectionException e) {
                return;
            }
            criteriaList.add(new Criteria(key).regex(value));

        });
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList));
        SortOperation sortOperation = new SortOperation(sort);

        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<Intervention> aggregationResults = this.mongoTemplate.aggregate(aggregation,"interventions",Intervention.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<Intervention> findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.of(this.mongoTemplate.findOne(query,Intervention.class));
    }


    public List<MaterialUsed> allmaterialUsed()
    {
        return mongoTemplate.findAll(Intervention.class).stream().flatMap(y-> y.getMaterialsToBeUsed().stream()).collect(Collectors.toList());
    }
    public int create(List<Intervention> interventionList)
    {
        return this.mongoTemplate.insertAll(interventionList).size();
    }

    @Override
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(Intervention.class);
    }
    private void checkIndex()
    {
        this.mongoTemplate.indexOps(Intervention.class).ensureIndex(
                new CompoundIndexDefinition(new Document()).on("title", Sort.Direction.ASC).unique()
        );
    }
    public Optional<Intervention> findInterventionByMaterial(Material material)
    {
        return mongoTemplate.findAll(Intervention.class).stream().filter(y->y.getStatus().equals(Status.In_Progress)&&y.getMaterialsToBeUsed().contains(material)).findFirst();
    }
}
