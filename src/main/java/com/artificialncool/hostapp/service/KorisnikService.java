package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.model.Korisnik;
import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import com.artificialncool.hostapp.repository.KorisnikRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class KorisnikService {

    private final KorisnikRepository korisnikRepository;

    public List<Korisnik> getAll() {
        return korisnikRepository.findAll();
    }

    public Korisnik getByUsername(String username) throws EntityNotFoundException{
        return korisnikRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("nema"));
    }

    public void createKorisnici() {
        korisnikRepository.save(Korisnik.builder()
                .ime("Petar")
                .prezime("Petrovic")
                .username("perce")
                .password("pass")
                .email("pera@gmail.com")
                .prebivalste("Uzice")
                .prosecnaOcena(4.55)
                .uloga(KorisnickaUloga.GUEST)
                .build()
        );

        korisnikRepository.save(Korisnik.builder()
                .ime("Nikola")
                .prezime("Nikolic")
                .username("majmuncina")
                .password("pass")
                .email("nikolic@gmail.com")
                .prebivalste("Loznica")
                .prosecnaOcena(2.55)
                .uloga(KorisnickaUloga.HOST)
                .build()
        );

        korisnikRepository.save(Korisnik.builder()
                .ime("Djordje")
                .prezime("Trnavcevic")
                .username("trle")
                .password("pass")
                .email("trle@gmail.com")
                .prebivalste("Uzice")
                .prosecnaOcena(4.55)
                .uloga(KorisnickaUloga.GUEST)
                .build()
        );

        korisnikRepository.save(Korisnik.builder()
                .ime("Lazar")
                .prezime("Jugovic")
                .username("Zola")
                .password("pass")
                .email("zoki@gmail.com")
                .prebivalste("Beograd")
                .prosecnaOcena(4.55)
                .uloga(KorisnickaUloga.HOST)
                .build()
        );
    }
}
