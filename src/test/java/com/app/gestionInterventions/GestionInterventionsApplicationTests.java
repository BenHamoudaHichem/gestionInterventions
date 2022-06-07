package com.app.gestionInterventions;

import com.app.gestionInterventions.exceptions.ResourceNotFoundException;
import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.EMeasure;
import com.app.gestionInterventions.models.additional.QuantityValue;
import com.app.gestionInterventions.models.recources.material.ECategory;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.MaterialUsed;
import com.app.gestionInterventions.models.recources.material.Status;
import com.app.gestionInterventions.models.recources.team.Team;
import com.app.gestionInterventions.models.user.User;
import com.app.gestionInterventions.models.user.role.ERole;
import com.app.gestionInterventions.models.user.role.Role;
import com.app.gestionInterventions.models.work.demand.Demand;
import com.app.gestionInterventions.models.work.intervention.Intervention;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.ICrud;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;

import com.app.gestionInterventions.repositories.resources.team.TeamRepositoryImpl;
import com.app.gestionInterventions.repositories.user.UserRepositoryImpl;
import com.app.gestionInterventions.repositories.user.role.RoleRepository;
import com.app.gestionInterventions.repositories.work.demand.DemandRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.category.CategoryRepositoryImpl;
import com.app.gestionInterventions.repositories.work.intervention.intervention.InterventionRepositoryImpl;
import com.app.gestionInterventions.services.FileUploadService;
import com.app.gestionInterventions.services.MailService;
import com.app.gestionInterventions.services.password.AESPasswordEncoder;
import com.app.gestionInterventions.services.GeocodeService;
import com.app.gestionInterventions.services.TNCitiesClient;
import com.app.gestionInterventions.services.statistics.DemandStatistic;
import com.app.gestionInterventions.services.statistics.MaterialStatistic;
import com.app.gestionInterventions.services.statistics.PairCustom;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.mongodb.assertions.Assertions;
import com.sun.mail.smtp.SMTPSendFailedException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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


	FileUploadService fileUploadService= new FileUploadService();
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
	void createMateriau()
	{
		Random random=new Random();
		Faker faker ;
		Material material;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		List<String>materialNameList=new ArrayList<>(getDistinctMaterieau());

		for (int i = 0; i <materialNameList.size() ; i++) {
			faker = new Faker();
			material=new Material(
					null,
					materialNameList.get(i),
					fakeValuesService.regexify("[A-Za-z]{165}"),
					 new QuantityValue(getRandomFloat(), EMeasure.Tons),

					faker.date().birthday(7,10),
					getAddress(faker),
					ECategory.Matter,
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
	void createMaterial()
	{
		Random random=new Random();
		Material material;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		List<String>materialNameList=new ArrayList<>(getDistinctMaterial());

		materialNameList.forEach(i->{

			for (int j = 1; j < random.nextInt(6)+3; j++) {
				Faker faker = new Faker();


				Assertions.assertNotNull(materialRepository.create(new Material(
						null,
						i,
						fakeValuesService.regexify("[A-Za-z]{165}"),
						new QuantityValue(getRandomFloat(), EMeasure.Unity),

						faker.date().birthday(7,10),
						getAddress(faker),
						ECategory.Material,
						Status.Functional

				)));
				System.out.println("created."+j);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		for (int i = 0; i <materialNameList.size() ; i++) {




		}


	}

	@Test
	public void createCategory() {
		getSomeCategoriesNames().forEach(i -> {
			Assertions.assertNotNull(this.categoryRepository.create(new ArrayList<Category>(
					Arrays.asList(new Category(null, i)
					)
			)));
		});

	}
	@Test
	public void createCustomer()
	{
		Faker faker=new Faker() ;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("fr", "FRANCE", "WIN"), new RandomService());
		User user;
		for (int i = 0; i <30 ; i++) {
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

		for (int i = 0; i <21 ; i++) {
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
		for (int i = 0; i < 50; i++) {
			faker = new Faker();
			demand = new Demand(
					null,
					fakeValuesService.regexify("[A-Za-z ]{15}"),
					fakeValuesService.regexify("[A-Za-z ]{250}"),
					this.getAddress(faker),
					com.app.gestionInterventions.models.work.demand.Status.In_Progress,
					users.get(ran.nextInt(users.size())),
					LocalDateTime.of(2022,ran.nextInt(4)+1,ran.nextInt(29)+1,10,30)
			);
			Assertions.assertNotNull(demandRepository.create(demand));
			System.out.println("Created "+i);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void createIntervention() throws ResourceNotFoundException {
		List<Demand>demandList;
		FakeValuesService fakeValuesService = new FakeValuesService(
				new Locale("en-GB"), new RandomService());
		Random ran = new Random();
		Faker faker;
		for (int i = 1; i < 10; i++) {
			faker=new Faker();
			demandList=demandRepository.findDemandsByStatus(com.app.gestionInterventions.models.work.demand.Status.In_Progress).subList(1,ran.nextInt(1)+3);

			Date startedAt=Date.from(LocalDate.of(2022,ran.nextInt(11)+1,ran.nextInt(27)+1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date expiredAt=Date.from(LocalDate.of(2023,ran.nextInt(11)+1,ran.nextInt(27)+1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			Assertions.assertNotNull(interventionRepository.create(new Intervention(
					null,
					fakeValuesService.regexify("[A-Za-z]{17}")+i,
					fakeValuesService.regexify("[A-Za-z ]{250}"),
					categoryRepository.all().get().get(ran.nextInt(4)),
					demandList.get(0).getAddress(),
					Date.from(demandList.get(0).getCreatedAt().plusMonths(2).toInstant(ZoneOffset.ofHours(+2))),
					expiredAt,
					demandList,
					getRandomMatterialList(),
					teamRepository.teamAvailable().get().get(0),
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
	public void somTests() throws ResourceNotFoundException, URISyntaxException, FileNotFoundException {

		File file = new File("C:\\Users\\hiche\\Downloads\\userData.csv");
		System.out.println(fileUploadService.serialize(file,User.class));
	}
	public List<String> getDistinctMaterieau()
	{
		return Arrays.asList("acier",
				"aluminium",
				"béton",
				"bitume",
				"préfabriqués en béton",
				"carrelage",
				"carreau de plâtre",
				"ciment",
				"granulat",
				"laine de roche",
				"laine de verre",
				"liant papier",
				"pavé",
				"plaque de plâtre",
				"PVC",
				"carreau de terre cuite",
				"bois",
				"Chaux",
				"mortier",
				"mortier adhésif",
				"terre cuite",
				"tuile",
				"brique",
				"verre",
				"plâtre",
				"plomb",
				"zinc");

	}
	public List<String> getDistinctMaterial()
	{
		return Arrays.asList("Bulldozer", "Scarificateur", "Scraper tracté ", "Niveleuse", "Chargeuse", "Excavateur", "Camion", "Centrale de préparation du béton", "Camion toupie", "Grues", "Echafaudage", "Pompe d’épuisement", "Pompe d’enduit et mortier");

	}
	public List<String> getMaterialValue()
	{
		String value;
		EMeasure eMeasure;
		Random random= new Random();

		if (random.nextBoolean()) {
			value=getDistinctMaterieau().get(random.nextInt(getDistinctMaterieau().size()));
			eMeasure=EMeasure.Tons;
		}
		else {
			value=getDistinctMaterial().get(random.nextInt(getDistinctMaterieau().size()));
			eMeasure=EMeasure.Unity;
		}
		return new ArrayList<String>(Arrays.asList(value,eMeasure.name()));
	}
	public Float getRandomFloat()
	{
		float leftLimit = 5000F;
		float rightLimit = 20001F;
		float res= leftLimit + new Random().nextFloat() * (rightLimit - leftLimit);

		return (float)Math.round(res*100.0f)/100.0f;
	}
	public List<String>getSomeCategoriesNames()
	{
		return Arrays.asList(
				"Entreprise publique","Sanitaires, fluides et climatisation",
				"Travaux de terrassement et de remblayage","pavage des routes"
		);
	}
	public List<MaterialUsed>getRandomMatterialList() throws ResourceNotFoundException {
		Random random= new Random();
		List<MaterialUsed>res=new ArrayList<>();
		ECategory eCategory=ECategory.Material;
		for (int i = 0; i < random.nextInt(3)+3; i++) {
			eCategory=ECategory.Material;
			if (i%2!=0) {
				eCategory=ECategory.Matter;
			}
			ECategory finalECategory = eCategory;
			List<MaterialUsed>randomList=materialRepository.availableMaterials().stream().filter(x->x.getCategory().equals(finalECategory)).map(y->
							new MaterialUsed(y.getId(),y.getName(),y.getDescription(),y.getTotalQuantity(),y.getDateOfPurchase(),y.getAddress(),y.getCategory(),y.getStatus(),
									new QuantityValue(y.getTotalQuantity().getQuantityToUse()/100.0f,y.getTotalQuantity().getMeasure()),LocalDateTime.now()))
					.collect(Collectors.toList());
			res.add(randomList.get(random.nextInt(4)));

		}
		return res;
	}
}


