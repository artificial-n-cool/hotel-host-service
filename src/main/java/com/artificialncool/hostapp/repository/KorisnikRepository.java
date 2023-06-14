package com.artificialncool.hostapp.repository;

import com.artificialncool.hostapp.model.Korisnik;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface KorisnikRepository extends MongoRepository<Korisnik, String> {

}
