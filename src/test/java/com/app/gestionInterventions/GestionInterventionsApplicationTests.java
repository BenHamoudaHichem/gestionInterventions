package com.app.gestionInterventions;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.EMeasure;
import com.app.gestionInterventions.models.additional.QuantityValue;
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
import com.app.gestionInterventions.services.MailService;
import com.app.gestionInterventions.services.password.AESPasswordEncoder;
import com.app.gestionInterventions.services.GeocodeService;
import com.app.gestionInterventions.services.TNCitiesClient;
import com.app.gestionInterventions.services.statistics.DemandStatistic;
import com.app.gestionInterventions.services.statistics.MaterialStatistic;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.mongodb.assertions.Assertions;
import com.sun.mail.smtp.SMTPSendFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
//@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class GestionInterventionsApplicationTests {

	PasswordEncoder passwordEncoder;
	@Autowired
	MailService mailService;
	DemandStatistic demandStatistic= new DemandStatistic();

    MaterialStatistic materialStatistic=new MaterialStatistic();

	@Autowired
	TNCitiesClient tnCitiesClient;


	GeocodeService geocodeService=new GeocodeService();

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

		this.passwordEncoder=new AESPasswordEncoder();

	}
	@Test
	static void example(@Autowired final MongoTemplate mongoTemplate) {
		Assertions.assertNotNull(mongoTemplate.getDb());
	}
	@Test
	public void createRole()
	{
		/*Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_CUSTOMER)));
		Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_MANAGER)));
		Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_MEMBER)));
		Assertions.assertNotNull(this.roleRepository.save(new Role(ERole.ROLE_TEAMMANAGER)));*/
		System.out.println(this.roleRepository.findAll());
	}
	@Test
	void createMaterial()
	{
		Random random=new Random();
		Faker faker ;
		Material material;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		List<String>materialNameList=new ArrayList<>(getDistinctMaterial());
		for (int i = 0; i <10 ; i++) {
			faker = new Faker();
			material=new Material(
					null,
					materialNameList.remove(random.nextInt(materialNameList.size())),
					fakeValuesService.regexify("[A-Za-z]{165}"),
					 new QuantityValue(getRandomFloat(), EMeasure.Meter),

					faker.date().birthday(7,10),
					getAddress(faker),
					Status.Functional

			);

			Assertions.assertNotNull(this.materialRepository.create(material));
			System.out.println("Materiel 1"+material.getName()+"created.");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}


	}

	@Test
	public void createCategory(){
		Assertions.assertNotNull(this.categoryRepository.create(new ArrayList<Category>(
				Arrays.asList(new Category(null,"Traveaux publique")
						//new Category(null,"Fuites")
						)
		)));}
	@Test
	public void createCustomer()
	{
		Faker faker=new Faker() ;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("fr", "FRANCE", "WIN"), new RandomService());
		User user;
		for (int i = 0; i <25 ; i++) {
			user=new User(
					null,
					faker.name().firstName(),
					faker.name().lastName(),
					fakeValuesService.regexify("[1-9]{2,10}")+"@gmail.com",
					passwordEncoder.encode(fakeValuesService.regexify("[1-9]{10}")),
					getAddress(faker),
					fakeValuesService.regexify("[0-9]{8}")
			);
			user.setRoles(new HashSet<Role>(Arrays.asList(roleRepository.findByName(ERole.ROLE_CUSTOMER).get())));
			Assertions.assertNotNull(this.userRepository.create(user));
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Test
	public void createManager()
	{
		Faker faker=new Faker() ;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("fr", "FRANCE", "WIN"), new RandomService());
		User user;
		for (int i = 0; i <1 ; i++) {
			user=new User(
					null,
					faker.name().firstName(),
					faker.name().lastName(),
					"22334455",
					passwordEncoder.encode("12345678"),
					getAddress(faker),
					fakeValuesService.regexify("[0-9]{8}")
			);
			user.setRoles(new HashSet<Role>(Arrays.asList(roleRepository.findByName(ERole.ROLE_MANAGER).get())));
			Assertions.assertNotNull(this.userRepository.create(user));

		}
	}
	@Test
	public void createMembers()
	{
		Faker faker=new Faker() ;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("fr", "FRANCE", "WIN"), new RandomService());
		User user;
		for (int i = 0; i <20 ; i++) {
			user=new User(
					null,
					faker.name().firstName(),
					faker.name().lastName(),
					fakeValuesService.regexify("[1-9]{8}"),
					passwordEncoder.encode(fakeValuesService.regexify("[a-z0-9]{8}")),
					getAddress(faker),
					fakeValuesService.regexify("[0-9]{8}")
			);
			user.setRoles(new HashSet<Role>(Arrays.asList(roleRepository.findByName(ERole.ROLE_MEMBER).get())));
			Assertions.assertNotNull(this.userRepository.create(user));
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}


	@Test
	public void createTeam()
	{
		Random ran = new Random();

		for (int i = 1; i <14 ; i++) {
			List<User>availableMembers=teamRepository.availableMembers();
			System.out.println(availableMembers.size());
			Team team=this.teamRepository.create(new Team(null,
					"equipe "+i,
					availableMembers.get(0),
					availableMembers.subList(1,ran.nextInt(3)+3),
					com.app.gestionInterventions.models.recources.team.Status.Available
			)).orElse(null);
			Assertions.assertNotNull(team);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}


	@Test
	public void createDemand() {
		Demand demand;
		Faker faker;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		Random ran = new Random();

		List<User>users=userRepository.findByRoLe(roleRepository.findByName(ERole.ROLE_CUSTOMER).get()).get();
		for (int i = 0; i < 20; i++) {
			faker = new Faker();
			demand = new Demand(
					null,
					fakeValuesService.regexify("[A-Za-z ]{15}"),
					fakeValuesService.regexify("[A-Za-z ]{250}"),
					this.getAddress(faker),
					com.app.gestionInterventions.models.work.demand.Status.In_Progress,
					users.get(ran.nextInt(users.size())),
					null
			);
			Assertions.assertNotNull(demandRepository.create(demand));
		}
	}

	@Test
	public void createIntervention() throws ResourceNotFoundException {
		List<Demand>demandList;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		Random ran = new Random();
		Faker faker;
		for (int i = 1; i < 4; i++) {
			faker=new Faker();
			demandList=demandRepository.findDemandsByStatus(com.app.gestionInterventions.models.work.demand.Status.In_Progress).subList(1,ran.nextInt(1)+4);

			Date startedAt=Date.from(LocalDate.of(2022,ran.nextInt(11)+1,ran.nextInt(29)+1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date expiredAt=Date.from(LocalDate.of(2023,ran.nextInt(11)+1,ran.nextInt(29)+1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			Assertions.assertNotNull(interventionRepository.create(new Intervention(
					null,
					fakeValuesService.regexify("[A-Za-z]{17}")+i,
					fakeValuesService.regexify("[A-Za-z ]{250}"),
					categoryRepository.all().get().get(ran.nextInt(2)),
					demandList.get(0).getAddress(),
					startedAt,
					expiredAt,
					demandList,
null,					teamRepository.teamAvailable().get().get(0),
					com.app.gestionInterventions.models.work.intervention.Status.In_Progress,
					null
			)));
			System.out.println("inserted "+i);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}



	@Test
	public void testMailService() throws SMTPSendFailedException{
	String to = "hichembenhamouda11@gmail.com";

	}
	private Address getAddress(Faker faker)
	{
		Random ran = new Random();
		int x = ran.nextInt(24) ;

		String state=this.tnCitiesClient.getStates().getData().get(ran.nextInt(24));

		List<String> cities=this.tnCitiesClient.getCitiesByState(state).getData();
		String city=cities.get(ran.nextInt(cities.size()));
		Address address= new Address(
				faker.address().zipCode(),
				faker.address().streetName(),
				city,
				state,
				"Tunisie",
				null
		);
		address.setLocation(geocodeService.fromCity(address));
		return address;
	}
	@Test
	public void somTests() {

		Object p= roleRepository.findByName(ERole.ROLE_MEMBER);
		System.out.println(((Role) p).getId());

	}
	public List<String> getDistinctMaterial()
	{
		return Arrays.asList("calcium", "phosphorus", "sodium", "potassium", "magnesium", "manganese", "sulfur", "chloride", "iron", "iodine", "fluoride", "zinc", "copper", "selenium", "chromium" , "cobalt");

	}
	public Float getRandomFloat()
	{
		float leftLimit = 500F;
		float rightLimit = 2001F;
		float res= leftLimit + new Random().nextFloat() * (rightLimit - leftLimit);

		return (float)Math.round(res*100.0f)/100.0f;
	}
	public List<String>getSomeCategoriesNames()
	{
		return Arrays.asList(
				"Contractualisation public","Sanitaires, fluides et climatisation",
				"Travaux de terrassement et de remblayage","pavage des routes"
		);
	}
}


