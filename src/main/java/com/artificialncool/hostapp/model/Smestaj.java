package com.artificialncool.hostapp.model;

import com.artificialncool.hostapp.model.helpers.Cena;
import com.artificialncool.hostapp.model.helpers.OcenaSmestaja;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@Document("smestaji")
public class Smestaj {
    @Id
    private String id;

    private String naziv;
    private String lokacija;
    private String pogodnosti;
    private String opis;
    private Integer minGostiju;
    private Integer maxGostiju;
    private Double prosecnaOcena;
    private Cena baseCena;

    private List<OcenaSmestaja> ocene = new ArrayList<>();
    private List<Promocija> promocije = new ArrayList<>();
    private List<Rezervacija> rezervacije = new ArrayList<>();

    private String vlasnikID;
}
