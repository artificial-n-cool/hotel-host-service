package com.artificialncool.hostapp.integration;

import com.artificialncool.hostapp.model.Korisnik;
import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import com.artificialncool.hostapp.repository.KorisnikRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.config.name=test")
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TestDatabaseCon {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    KorisnikRepository korisnikRepository;


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

    @BeforeEach
    void setUp() {
        korisnikRepository.deleteAll();
    }

    @Test
    public void shouldSaveOne() throws Exception {
        Korisnik korisnik = Korisnik.builder()
                .username("test-user")
                .password("test-pass")
                .email("test@example.com")
                .ime("testko")
                .prezime("testovic")
                .uloga(KorisnickaUloga.GUEST)
                .prebivalste("Buljakovac 34, Cacak")
                .build();


        mockMvc.perform(post("/api/host/korisnik")
                        .content(new ObjectMapper().writeValueAsString(korisnik))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("test-user"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.ime").value("testko"));
    }

    @Test
    public void shouldReturnAll() throws Exception {
        korisnikRepository.saveAll(List.of(new Korisnik(), new Korisnik(), new Korisnik()));

        mockMvc.perform(get("/api/host/korisnik/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }
}
