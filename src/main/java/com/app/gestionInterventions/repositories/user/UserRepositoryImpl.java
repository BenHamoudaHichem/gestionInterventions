package com.app.gestionInterventions.repositories.user;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.tools.Stashed;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;

import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.repositories.tools.StashedRepository;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import com.mongodb.DBRef;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom{
    private final MongoTemplate mongoTemplate;
    @Autowired
    private RoleRepository roleRepository;
    private StashedRepository stashedRepository;
    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.stashedRepository=new StashedRepository(mongoTemplate);
    }

    @Override
    public Optional<User> create(User user) {
        this.checkIndex();
        return Optional.of(this.mongoTemplate.save(user,"users"));
    }

    @Override
    public long update(String id,@NotNull User user) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update =new Update();

        if(!user.getFirstName().isEmpty()) {
            update.set("firstName", user.getFirstName());
        }
        if(!user.getLastName().isEmpty()) {
            update.set("lastName", user.getLastName());
        }
        if(user.getAddress()!=null) {
            update.set("address", user.getAddress());
        }
        if(!user.getTel().isEmpty()) {
            update.set("tel", user.getTel());
        }
        if(!user.getRoles().isEmpty()) {
            Set<DBRef> roles= user.getRoles().stream().map(x->new DBRef("roles",new ObjectId(x.getId()))).collect(Collectors.toSet());
            update.set("roles", roles);
        }
        return this.mongoTemplate.updateFirst(query,update, User.class).getModifiedCount();
    }

    @Override
    public long detele(String id) {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        stashedRepository.create(new Stashed(null,this.mongoTemplate.findOne(query, User.class), LocalDateTime.now()));

        return mongoTemplate.remove(Objects.requireNonNull(this.mongoTemplate.findOne(query, User.class))).getDeletedCount();

    }


    @Override
    public Optional<List<User>> all() {
        return Optional.of(this.mongoTemplate.findAll(User.class));
    }

    @Override
    public Optional<List<User>> searchAnd(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((key,value)->{
            try {
                if(Arrays.stream(Introspector.getBeanInfo(Address.class).getPropertyDescriptors()).filter(x->(!x.getName().equals("class"))).map(x->x.getName()).collect(Collectors.toList()).contains(key)){
                    criteriaList.add(new Criteria("address."+key).regex(value));
                    return;
                }
                if (key.contains("role")) {
                    Role role;
                    switch (value) {
                        case "manager":
                            role = roleRepository.findByName(ERole.ROLE_MANAGER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            break;

                        case "member":
                            role = roleRepository.findByName(ERole.ROLE_MEMBER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            break;

                        case "tm":
                            role = roleRepository.findByName(ERole.ROLE_TEAMMANAGER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            break;

                        default:
                            role = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    }
                    criteriaList.add(new Criteria("roles").all(new DBRef("roles",new ObjectId(role.getId()))));
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
        AggregationResults<User> aggregationResults = this.mongoTemplate.aggregate(aggregation,"users",User.class);
        return Optional.of(aggregationResults.getMappedResults());

    }

    @Override
    public Optional<User> findById(String id) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(id));
        return Optional.ofNullable(this.mongoTemplate.findOne(query, User.class));
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
        Query query= new Query();
        query.addCriteria(Criteria.where("identifier").is(identifier));
        return Optional.ofNullable(this.mongoTemplate.findOne(query,User.class));
    }

    @Override
    public Optional<List<User>> searchOr(Map<String, String> entries, Sort sort) {
        List<Criteria> criteriaList= new ArrayList<>();
        entries.forEach((key,value)->{
            if (key.contains("role")) {
                Role role;
                switch (value) {
                    case "manager":
                        role = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        break;
                    case "member":
                        role = roleRepository.findByName(ERole.ROLE_MEMBER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        break;
                    case "tm":
                        role = roleRepository.findByName(ERole.ROLE_TEAMMANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        break;
                    default:
                        role = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                }
                criteriaList.add(new Criteria("roles").all(new DBRef("roles",new ObjectId(role.getId()))));
                return;
            }
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
        MatchOperation matchOperation = Aggregation.match(new Criteria().orOperator(criteriaList));

        Aggregation aggregation = newAggregation(sortOperation,matchOperation);
        AggregationResults<User> aggregationResults = this.mongoTemplate.aggregate(aggregation,"users",User.class);
        return Optional.of(aggregationResults.getMappedResults());

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

    public Optional<List<User>> findByRoLe(Role role)
    {
        Query query=new Query();
        query.addCriteria(Criteria.where("roles").all(new DBRef("roles",new ObjectId(role.getId()))));
        return Optional.ofNullable(this.mongoTemplate.find(query,User.class));
    }

    public long countByRole(Role role)
    {
        Query query=new Query();
        query.addCriteria(Criteria.where("roles.$id").is(new ObjectId(role.getId())));
        return this.mongoTemplate.count(query, User.class);
    }

    public long changePassword(User user)
    {
        Query query= new Query();
        query.addCriteria(Criteria.where("_id").is(user.getId()));
        Update update =new Update();
        update.set("password", user.getPassword());

        return this.mongoTemplate.updateFirst(query,update, User.class).getModifiedCount();

    }



    private void checkIndex()
    {
        this.mongoTemplate.indexOps(User.class).ensureIndex(
                new CompoundIndexDefinition(new Document()).on("identifier", Sort.Direction.ASC).unique()
        );
        this.mongoTemplate.indexOps(User.class).ensureIndex(
                new CompoundIndexDefinition(new Document()).on("tel", Sort.Direction.ASC).unique()
        );
    }
}
