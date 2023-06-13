package com.artificialncool.hostapp.repository;

import com.artificialncool.hostapp.model.Korisnik;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface KorisnikRepository extends MongoRepository<Korisnik, Long> {
    Optional<Korisnik> findByUsername(String username);

    Optional<Korisnik> findByEmail(String email);

    long deleteByID(Long ID);

    long deleteByUsername(String username);

    Korisnik findByPrebivalsteContainsIgnoreCase(String prebivalste);
}
