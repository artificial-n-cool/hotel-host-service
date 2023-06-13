package com.artificialncool.hostapp.model;

import com.artificialncool.hostapp.model.helpers.Cena;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Document("smestaji")
public class Smestaj {
    @Id
    private Long ID;

    private String naziv;
    private String lokacija;
    private String pogodnosti;
    private String opis;
    private Integer minGostiju;
    private Integer maxGostiju;
    private Double prosecnaOcena;

    @DocumentReference
    private Cena baseCena;

    @DocumentReference
    private Korisnik vlasnik;
}
