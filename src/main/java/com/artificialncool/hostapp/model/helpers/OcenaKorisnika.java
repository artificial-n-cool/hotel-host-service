package com.artificialncool.hostapp.model.helpers;

import com.artificialncool.hostapp.model.Korisnik;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Document("ocenekorisnika")
public class OcenaKorisnika {
    @Id
    private Long ID;

    private Double ocena;
    private LocalDateTime datum;

    @DocumentReference
    private Korisnik ocenjivac;

    @DocumentReference
    private Korisnik host;
}
