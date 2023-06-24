package com.artificialncool.hostapp.integration;

import com.artificialncool.hostapp.dto.converter.SmestajConverter;
import com.artificialncool.hostapp.dto.model.PromocijaDTO;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Promocija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.TipCene;
import com.artificialncool.hostapp.model.helpers.Cena;
import com.artificialncool.hostapp.repository.SmestajRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;


import java.time.DayOfWeek;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = "spring.config.name=test")
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TestSmestajIntegration {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    SmestajRepository smestajRepository;

    @Autowired
    SmestajConverter smestajConverter;

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
        smestajRepository.deleteAll();
    }

    @Test
    public void shouldSaveOne() throws Exception{
        SmestajDTO smestaj = SmestajDTO.builder()
                .naziv("Apartmani Brdo")
                .lokacija("Banovo Brdo")
                .pogodnosti("Wifi, Klima, Internet, Kablovska")
                .opis("Halo najace")
                .baseCena(15.)
                .tipCene(TipCene.PO_SMESTAJU)
                .vlasnikID("4025ti4j042tu")
                .minGostiju(1)
                .maxGostiju(3)
                .build();

        mockMvc.perform(post("/api/host/smestaj")
                .content(new ObjectMapper().writeValueAsString(smestaj))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.baseCena").value(15.));

        assertEquals(smestajRepository.findByNazivContainsIgnoreCase("Apartmani Brdo").size(), 1);
        assertNotNull(smestajRepository.findByNazivContainsIgnoreCase("Apartmani Brdo").get(0).getId());
    }

    @Test
    public void shouldUpdate() throws Exception {
        String id = "1";
        Smestaj smestaj = Smestaj.builder()
                .id(id)
                .naziv("Apartmani Brdo")
                .lokacija("Banovo Brdo")
                .pogodnosti("Wifi, Klima, Internet, Kablovska")
                .opis("Halo najace")
                .baseCena(
                        Cena.builder()
                                .cena(15.)
                                .tipCene(TipCene.PO_SMESTAJU)
                                .build()
                )
                .vlasnikID("4025ti4j042tu")
                .minGostiju(1)
                .maxGostiju(3)
                .build();

        smestajRepository.save(smestaj);

        assertTrue(smestajRepository.findById(id).isPresent());
        assertNotNull(smestajRepository.findById(id).get().getId());

        smestaj.setMaxGostiju(5);

        mockMvc.perform(put("/api/host/smestaj")
                        .content(new ObjectMapper().writeValueAsString(smestajConverter.toDTO(smestaj)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.maxGostiju").value(5));

    }

    @Test
    public void shouldAddPromotion() throws Exception {
        String id = "1";
        Smestaj smestaj = Smestaj.builder()
                .id(id)
                .naziv("Apartmani Brdo")
                .lokacija("Banovo Brdo")
                .pogodnosti("Wifi, Klima, Internet, Kablovska")
                .opis("Halo najace")
                .baseCena(
                        Cena.builder()
                                .cena(15.)
                                .tipCene(TipCene.PO_SMESTAJU)
                                .build()
                )
                .vlasnikID("4025ti4j042tu")
                .minGostiju(1)
                .maxGostiju(3)
                .build();

        smestajRepository.save(smestaj);

        PromocijaDTO promocijaDTO = PromocijaDTO.builder()
                .smestajId(id)
                .procenat(0.2)
                .dani(List.of(DayOfWeek.SATURDAY.getValue(), DayOfWeek.SUNDAY.getValue()))
                .datumOd("2023-07-01")
                .datumDo("2023-09-01")
                .build();

        mockMvc.perform(put("/api/host/smestaj/promocija")
                .content(new ObjectMapper().writeValueAsString(promocijaDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        assertEquals(1, smestajRepository.findById(id).get().getPromocije().size());
        Promocija p = smestajRepository.findById(id).get().getPromocije().get(0);
        assertNotNull(p.getId());
    }


}
