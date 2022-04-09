package com.app.gestionInterventions;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.Location;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.Status;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;

import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.category.CategoryRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.intervention.InterventionRepositoryImpl;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.mongodb.assertions.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.*;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class GestionInterventionsApplicationTests {
	PasswordEncoder passwordEncoder;


	MaterialRepositoryImpl materialRepository;
	UserRepositoryImpl userRepository;
	@Autowired
	RoleRepository roleRepository;
	DemandRepositoryImpl demandRepository;
	InterventionRepositoryImpl interventionRepository;
	CategoryRepositoryImpl categoryRepository;
	TeamRepositoryImpl teamRepository;


	@Autowired MongoTemplate mongoTemplate;
	@BeforeEach
	public  void initialize(@Autowired final MongoTemplate mongoTemplate)
	{

		this.demandRepository= new DemandRepositoryImpl(mongoTemplate);
		this.materialRepository=new MaterialRepositoryImpl(mongoTemplate);
		this.userRepository =new UserRepositoryImpl(mongoTemplate);
		this.categoryRepository =new CategoryRepositoryImpl(mongoTemplate);
		this.interventionRepository =new InterventionRepositoryImpl(mongoTemplate);
		this.teamRepository =new TeamRepositoryImpl(mongoTemplate);

		this.passwordEncoder=new BCryptPasswordEncoder();

	}
	@Test
	static void example(@Autowired final MongoTemplate mongoTemplate) {
		Assertions.assertNotNull(mongoTemplate.getDb());
	}

	@Test
	void createMaterial()
	{
		Faker faker ;
		Material material;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		for (int i = 0; i <10 ; i++) {
			 faker = new Faker();

			String streetName = faker.address().streetName();
			String state = faker.address().state();
			String city = faker.address().city();
			String country = faker.address().country();
			Address address=new Address(
					faker.address().zipCode(),
					faker.address().streetName(),
					faker.address().cityName(),
					faker.address().state(),
					faker.address().country(),
					new Location(
							faker.address().longitude().replace(",","."),
							faker.address().latitude().replace(",",".")
					)
			);
			material=new Material(
					null,
					fakeValuesService.regexify("[a-z1-9]{10}"),
					fakeValuesService.regexify("[a-z1-9]{50}"),
					faker.date().birthday(),
					address,
					Status.Functional

			);
			Assertions.assertNotNull(this.materialRepository.create(material));

		}


	}
	@Test
	public void createUser()
	{
		Faker faker=new Faker() ;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		User user;
		for (int i = 0; i <1 ; i++) {
			user=new User(
					null,
					faker.name().firstName(),
					faker.name().lastName(),
					"11223344",
					//fakeValuesService.regexify("[1-9]{8}"),
					passwordEncoder.encode("12345678"),
					getAddress(faker),
					fakeValuesService.regexify("[1-9]{8}")
					);
			user.setRoles(new HashSet<Role>(Arrays.asList(roleRepository.findByName(ERole.ROLE_CUSTOMER).get())));
			Assertions.assertNotNull(this.userRepository.create(user));

		}
	}
	private Address getAddress(Faker faker)
	{

		return new Address(
				faker.address().zipCode(),
				faker.address().streetName(),
				faker.address().cityName(),
				faker.address().state(),
				faker.address().country(),
				new Location(
						faker.address().longitude().replace(",","."),
						faker.address().latitude().replace(",",".")
				)
		);
	}

	@Test
	public void createTeam()
	{

		Team team=this.teamRepository.create(new Team(null,
				"equipe 2",
				this.userRepository.findById("6250e5cf9345d57e85022bfe").get(),
				(this.userRepository.findByRoLe(roleRepository.findByName(ERole.ROLE_MEMBER).get()).get().subList(1,3)),
				com.app.gestionInterventions.models.recources.team.Status.Available
				)).orElse(null);
		Assertions.assertNotNull(team);

		}

		@Test
	public void createRole()
		{
			Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_CUSTOMER)));
			Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_MANAGER)));
			Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_MEMBER)));
			Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_TEAMMANAGER)));
		}
	@Test
	public void createDemand() {
		Demand demand;
		Faker faker;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());

		for (int i = 0; i < 3; i++) {
			faker = new Faker();
			demand = new Demand(
					null,
					fakeValuesService.regexify("[A-Za-z ]{15}"),
					fakeValuesService.regexify("[A-Za-z]{55}"),
					this.getAddress(faker),
					com.app.gestionInterventions.models.work.demand.Status.In_Progress,
					userRepository.findById("625105651cd6613649540f8c").get(),
					null
			);
			Assertions.assertNotNull(demandRepository.create(demand));
		}
	}
	@Test
	public void createCategory(){
		Assertions.assertNotNull(this.categoryRepository.create(new ArrayList<Category>(
				Arrays.asList(new Category(null,"Traveaux publique"),
				new Category(null,"Fuites"))
		)));


	}
	@Test
	public void somTests()
	{
		System.out.println(this.userRepository.findByRoLe(roleRepository.findByName(ERole.ROLE_TEAMMANAGER).get()).get().size());
	//	Assertions.assertNotNull(this.userRepository.findByRoLe(roleRepository.findByName(ERole.ROLE_TEAMMANAGER).get()));
	}
	}


