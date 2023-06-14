package com.artificialncool.hostapp.repository;

import com.artificialncool.hostapp.model.Korisnik;
import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface KorisnikRepository extends MongoRepository<Korisnik, String> {
    Optional<Korisnik> findByUsername(String username);

    Optional<Korisnik> findByEmailIgnoreCase(String email);

    List<Korisnik> findAllByUloga(KorisnickaUloga uloga);

    List<Korisnik> findByProsecnaOcenaGreaterThanEqual(Double criteria);

    long deleteByUsername(String username);

    long deleteByEmailIgnoreCase(String email);
}
