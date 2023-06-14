package com.artificialncool.hostapp.model;

import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private String password;
    private String ime;
    private String prezime;

    @Indexed(unique = true)
    private String email;

    private String prebivalste;
    private Double prosecnaOcena;
    private KorisnickaUloga uloga;
}
