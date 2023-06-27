package com.artificialncool.hostapp.integration;

import com.artificialncool.hostapp.dto.converter.SmestajConverter;
import com.artificialncool.hostapp.dto.model.RezervacijaDTO;
import com.artificialncool.hostapp.model.Rezervacija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.StatusRezervacije;
import com.artificialncool.hostapp.model.enums.TipCene;
import com.artificialncool.hostapp.model.helpers.Cena;
import com.artificialncool.hostapp.repository.SmestajRepository;
import com.artificialncool.hostapp.service.RezervacijaService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestPropertySource("classpath:test.properties")
public class TestRezervacijeIntegration {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    SmestajRepository smestajRepository;

    @Autowired
    SmestajConverter smestajConverter;

    @Autowired
    RezervacijaService rezervacijaService;

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
    public void cleanUp() {
        smestajRepository.deleteAll();
    }

    @Test
    public void shouldAddReservation() throws Exception{
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

        RezervacijaDTO rezervacijaDTO = RezervacijaDTO.builder()
                .datumOd("2023-01-07")
                .datumDo("2023-08-07")
                .smestajID(id)
                .korisnikID("4025ti4j042tu")
                .statusRezervacije("U_OBRADI")
                .brojOsoba(3)
                .build();

        mockMvc.perform(post("/api/host/rezervacije")
                        .content(new ObjectMapper().writeValueAsString(rezervacijaDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Smestaj s = smestajRepository.findById(id).get();
        assertEquals(1, s.getRezervacije().size());
        assertNotNull(s.getRezervacije().get(0).getId());
        assertEquals(s.getRezervacije().get(0).getStatusRezervacije(), StatusRezervacije.U_OBRADI);
    }

    @Test
    public void shouldAcceptReservation() throws Exception {
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

        RezervacijaDTO rezervacijaDTO = RezervacijaDTO.builder()
                .datumOd("2023-01-07")
                .datumDo("2023-08-07")
                .smestajID(id)
                .statusRezervacije("U_OBRADI")
                .korisnikID("4025ti4j042tu")
                .brojOsoba(3)
                .build();

        mockMvc.perform(post("/api/host/rezervacije")
                .content(new ObjectMapper().writeValueAsString(rezervacijaDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        Rezervacija r = smestajRepository.findById(id).get().getRezervacije().get(0);
        String url = String.format("/api/host/rezervacije/accept/%s/%s", r.getId(), id);
        mockMvc.perform(put(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.statusRezervacije").value("PRIHVACENO"));
    }

    @Test
    public void shouldRejectReservation() throws Exception {
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

        RezervacijaDTO rezervacijaDTO_1 = RezervacijaDTO.builder()
                .datumOd("2023-01-07")
                .datumDo("2023-08-07")
                .smestajID(id)
                .statusRezervacije("U_OBRADI")
                .korisnikID("4025ti4j042tu")
                .brojOsoba(3)
                .build();

        mockMvc.perform(post("/api/host/rezervacije")
                .content(new ObjectMapper().writeValueAsString(rezervacijaDTO_1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        String rezId = smestajRepository.findById(id).get().getRezervacije().get(0).getId();
        String url = String.format("/api/host/rezervacije/accept/%s/%s", rezId, id);
        mockMvc.perform(put(url)).andExpect(status().isOk());


        RezervacijaDTO rezervacijaDTO_2 = RezervacijaDTO.builder()
                .datumOd("2023-04-07")
                .datumDo("2023-12-07")
                .smestajID(id)
                .statusRezervacije("U_OBRADI")
                .korisnikID("4025ti4j042tu")
                .brojOsoba(3)
                .build();

        mockMvc.perform(post("/api/host/rezervacije")
                .content(new ObjectMapper().writeValueAsString(rezervacijaDTO_2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isConflict());
    }
}
