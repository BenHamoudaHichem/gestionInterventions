package com.app.gestionInterventions.repositories.resources.team;

import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.Status;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
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
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

@Repository
public class TeamRepositoryImpl implements TeamRepositoryCustom{
    private final MongoTemplate mongoTemplate;

    UserRepositoryImpl userRepository;
    @Autowired
    public TeamRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository=new UserRepositoryImpl(mongoTemplate);
    }

    @Override
    public Optional<Team> create(Team team) {
        Query query=new Query();
        query.addCriteria(Criteria.where("name").regex("ROLE_TEAMMANAGER"));
        Role role=this.mongoTemplate.findOne(query,Role.class);

        this.checkIndex();
        team.getManager().setRoles(new HashSet<Role>(Arrays.asList(role)));

        this.userRepository.update(team.getManager().getId(),team.getManager());
        return Optional.ofNullable(this.mongoTemplate.save(team));
    }

    @Override
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
    public Optional<List<Team>> search(String key, String value) {

        MatchOperation matchOperation = Aggregation.match(new Criteria(key).regex(value));
        Aggregation aggregation = newAggregation(matchOperation);
        AggregationResults<Team> aggregationResults = this.mongoTemplate.aggregate(aggregation,"teams",Team.class);
        return Optional.of(aggregationResults.getMappedResults());

    }
    @Override
    public boolean isAvailable(Team team) {
        Query query= new Query();
        query.addCriteria(new Criteria("team").is(team).and("status").ne(Status.Completed));
        return !this.mongoTemplate.exists(query, Intervention.class);
    }

    @Override
    public Optional<List<Team>> teamAvailable() {
        Query query= new Query();
        query.addCriteria(new Criteria("status").ne(Status.Completed));
        List<Team> teams = new ArrayList<Team>();
        teams = this.mongoTemplate.findAll(Team.class);
        teams.removeAll(
                this.mongoTemplate.find(query,Intervention.class).stream()
                        .map(intervention -> intervention.getTeam()).collect(Collectors.toList())
        );
        return Optional.of(teams);
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
}
