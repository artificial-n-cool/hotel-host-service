package com.artificialncool.hostapp.integration;

import com.artificialncool.hostapp.dto.converter.SmestajConverter;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.TipCene;
import com.artificialncool.hostapp.model.helpers.Cena;
import com.artificialncool.hostapp.repository.SmestajRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestSmestajIntegration extends AbstractIntegrationTest{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    SmestajRepository smestajRepository;

    @Autowired
    SmestajConverter smestajConverter;

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

}
