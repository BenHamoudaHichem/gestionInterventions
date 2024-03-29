package com.app.gestionInterventions.repositories.resources.team;


import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.team.Status;
import com.app.gestionInterventions.models.recources.team.Team;

import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.models.work.intervention.Intervention;

import com.app.gestionInterventions.repositories.tools.StashedRepository;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.mongodb.DBRef;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
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
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class TeamRepositoryImpl implements TeamRepositoryCustom{
    private final MongoTemplate mongoTemplate;
    private StashedRepository stashedRepository;

    private UserRepositoryImpl userRepository;
    @Autowired
    public TeamRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository=new UserRepositoryImpl(mongoTemplate);
        this.stashedRepository=new StashedRepository(mongoTemplate);

    }

    @Override
    public Optional<Team> create(Team team) {
        Query query=new Query();
        query.addCriteria(Criteria.where("name").is(ERole.ROLE_TEAMMANAGER.name()));
        Role role=this.mongoTemplate.findOne(query,Role.class);

        this.checkIndex();
        User manager =userRepository.findById(team.getManager().getId()).get();

        manager.setRoles(new HashSet<Role>(Arrays.asList(role)));

        this.userRepository.update(manager.getId(),manager);
        team.getMembers().removeIf(x->x.equals(manager));
        return Optional.ofNullable(this.mongoTemplate.insert(team));
    }

    public long update(String id, Team team) {

        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update =new Update();
        if (team.getMembers().contains(team.getManager())){
            team.getMembers().remove(team.getManager());
        }
        update.set("name",team.getName());
        update.set("manager",new DBRef("users",new ObjectId(team.getManager().getId())));
        update.set("status",team.getStatus());
        update.set("members",team.getMembers().stream().map(x->new DBRef("users",new ObjectId(x.getId()))).collect(Collectors.toList()));
        return this.mongoTemplate.updateFirst(query,update, Team.class).getModifiedCount();

    }

    @Override
    public long detele(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        stashedRepository.create(new Stashed<>(null,this.mongoTemplate.findOne(query, Team.class), LocalDateTime.now()));

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
        return Optional.ofNullable(this.mongoTemplate.findOne(query,Team.class));
    }

    @Override
    public Optional<List<Team>> searchOr(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((key,value)->{

                try {
                    if(Arrays.stream(Introspector.getBeanInfo(User.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class")&&!x.getName().equals("roles")&&!x.getName().equals("address"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                        criteriaList.add(new Criteria("manager."+key).regex(value));
                        return;
                    }
                } catch (IntrospectionException e) {
                    return;
                }
                try {
                    if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->!x.getName().equals("class")).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                        criteriaList.add(new Criteria("manager.address."+key).regex(value));
                    }
                } catch (IntrospectionException e) {
                    return;
                }
            criteriaList.add(new Criteria(key).regex(value));
        });
        MatchOperation matchOperation = Aggregation.match(new Criteria().orOperator(criteriaList));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<Team> aggregationResults = this.mongoTemplate.aggregate(aggregation,"teams",Team.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public Optional<List<Team>> searchAnd(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((key,value)->{

            try {
                if(Arrays.stream(Introspector.getBeanInfo(User.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class")&&!x.getName().equals("roles")&&!x.getName().equals("address"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("manager."+key).regex(value));
                    return;
                }
            } catch (IntrospectionException e) {
                return;
            }
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->!x.getName().equals("class")).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("manager.address."+key).regex(value));
                }
            } catch (IntrospectionException e) {
                return;
            }
            criteriaList.add(new Criteria(key).regex(value));
        });
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<Team> aggregationResults = this.mongoTemplate.aggregate(aggregation,"teams",Team.class);
        return Optional.of(aggregationResults.getMappedResults());
    }

    @Override
    public boolean isAvailable(Team team) {
        Query query= new Query();
        query.addCriteria( Criteria.where("status").is(Status.Available));
        return !this.mongoTemplate.exists(query, Intervention.class);
    }

    @Override
    public Optional<List<Team>> teamAvailable() {
        Query query= new Query();
        query.addCriteria(Criteria.where("status").is(Status.Available));

        return Optional.of(this.mongoTemplate.find(query,Team.class));
    }

    @Override
    public long nbIntervention(Team team) {
        Query query= new Query();
        query.addCriteria(new Criteria("team").is(team));
        return this.mongoTemplate.count(query,Intervention.class);
    }
    @Override
    public  void dropCollection()
    {
        this.mongoTemplate.dropCollection(Team.class);
    }
    private void checkIndex()
    {
        this.mongoTemplate.indexOps(Team.class).ensureIndex(
                new CompoundIndexDefinition(new Document()).on("name", Sort.Direction.ASC).unique()
        );
    }
    public long countTeamByStatus(com.app.gestionInterventions.models.recources.team.Status status)
    {
        Query query= new Query();

        if (status == null) {
            return this.mongoTemplate.count(query,Team.class);
        }
        query.addCriteria(Criteria.where("status").is(status));
        return this.mongoTemplate.count(query, Team.class);
    }
    public List<User> allMembers(){
        if(!mongoTemplate.collectionExists(Team.class))
        {
            return new ArrayList<User>();
        }
        return this.mongoTemplate.findAll(Team.class).stream().map(x->x.getMembers()).flatMap(List::stream).collect(Collectors.toList());
    }
    public List<User> availableMembers(){
        Query query= new Query();query.addCriteria(new Criteria()
                .orOperator(Criteria.where("name").is(ERole.ROLE_TEAMMANAGER.name()),
                        Criteria.where("name").is(ERole.ROLE_MEMBER.name())));
        Query query1= new Query();
        query1.addCriteria(Criteria.where("roles").in(mongoTemplate.find(query,Role.class).stream().map(x->new DBRef("roles",new ObjectId(x.getId()))).collect(Collectors.toList())));

        if(!mongoTemplate.collectionExists(Team.class))
        {
            return mongoTemplate.find(query1,User.class);
        }
       return this.mongoTemplate.find(query1,User.class).stream().filter(p->!new ArrayList<User>(Stream.concat(this.mongoTemplate.findAll(Team.class).stream().filter(x->!x.getStatus().equals(Status.Available)).map(x->x.getMembers()).flatMap(List::stream),this.mongoTemplate.findAll(Team.class).stream().filter(x->!x.getStatus().equals(Status.Available)).map(x->x.getManager()))
               .collect(Collectors.toList())).contains(p)
        ).collect(Collectors.toList());

    }
    public Optional<Intervention>findCurrentIntervention(String id)
    {
        Query query= new Query();
        query.addCriteria(new Criteria().andOperator(Criteria.where("team").is(new DBRef("teams",id)),Criteria.where("status").is(com.app.gestionInterventions.models.work.intervention.Status
                .In_Progress.name())));
        return Optional.ofNullable(mongoTemplate.findOne(query,Intervention.class));

    }
}
