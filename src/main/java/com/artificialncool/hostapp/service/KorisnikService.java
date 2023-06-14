package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.model.Korisnik;
import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import com.artificialncool.hostapp.repository.KorisnikRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Korisnik getByEmail(String email) throws EntityNotFoundException {
        return korisnikRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("nema korisnika"));
    }

    public List<Korisnik> getAllByUloga(KorisnickaUloga uloga) {
        return korisnikRepository
                .findAllByUloga(uloga);
    }

    public List<Korisnik> getAllHosts() {
        return getAllByUloga(KorisnickaUloga.HOST);
    }

    public List<Korisnik> getAllGuests() {
        return getAllByUloga(KorisnickaUloga.GUEST);
    }

    public List<Korisnik> getAllRatedBetterThan(Double rating) {
        return korisnikRepository.findByProsecnaOcenaGreaterThanEqual(rating);
    }

    public Korisnik saveKorisnik(Korisnik korisnik) {
        return korisnikRepository.save(korisnik);
    }

    public void initDb() {
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
