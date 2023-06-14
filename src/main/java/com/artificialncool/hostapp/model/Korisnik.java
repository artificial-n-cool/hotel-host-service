package com.artificialncool.hostapp.model;

import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import com.artificialncool.hostapp.model.helpers.OcenaKorisnika;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter @Setter
@Builder
@Document("korisnici")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Korisnik {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private String ime;
    private String prezime;

    @Indexed(unique = true)
    private String email;

    private String prebivalste;
    private Double prosecnaOcena;
    private KorisnickaUloga uloga;

    private List<OcenaKorisnika> ocene;
    private List<Notifikacija> notifikacije;
}
