package com.artificialncool.hostapp.repository;

import com.artificialncool.hostapp.model.Rezervacija;
import com.artificialncool.hostapp.model.Smestaj;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SmestajRepository extends MongoRepository<Smestaj, String> {
    List<Smestaj> findByNazivContainsIgnoreCase(String naziv);

    List<Smestaj> findByLokacijaContainsIgnoreCase(String lokacija);

    List<Smestaj> findByProsecnaOcenaGreaterThanEqual(Double criteria);

    List<Smestaj> findByVlasnikID(String vlasnikID);

    long deleteAllByVlasnikID(String vlasnikID);
}
