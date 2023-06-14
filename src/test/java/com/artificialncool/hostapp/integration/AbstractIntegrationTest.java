package com.artificialncool.hostapp.integration;

import com.artificialncool.hostapp.repository.KorisnikRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class AbstractIntegrationTest {

    static MongoDBContainer mongo
            = new MongoDBContainer(DockerImageName.parse("mongo:latest"));


    /*
        - SPRING_DATA_MONGODB_HOST=mongo-db
        - SPRING_DATA_MONGODB_PORT=27017
        - SPRING_DATA_MONGODB_DATABASE=test-db
     */
    @DynamicPropertySource
    static void configureDBConnection(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongo::getHost);
        registry.add("spring.data.mongodb.port", mongo::getFirstMappedPort);
    }

    @BeforeAll
    static void beforeAll() {
        mongo.start();
    }

    @AfterAll
    static void afterAll() {
        mongo.stop();
    }

}
