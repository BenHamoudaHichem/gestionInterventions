package com.app.gestionInterventions;

import com.app.gestionInterventions.models.additional.Address;
import com.app.gestionInterventions.models.additional.Location;
import com.app.gestionInterventions.models.recources.material.Material;
import com.app.gestionInterventions.models.recources.material.Status;
import com.app.gestionInterventions.models.work.intervention.category.Category;
import com.app.gestionInterventions.repositories.resources.material.MaterialRepositoryImpl;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.Locale;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class GestionInterventionsApplicationTests {


	MaterialRepositoryImpl materialRepository;

	@Autowired  MongoTemplate mongoTemplate;
	@BeforeEach
	public  void initialize(@Autowired final MongoTemplate mongoTemplate)
	{
		this.materialRepository=new MaterialRepositoryImpl(mongoTemplate);

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

}
