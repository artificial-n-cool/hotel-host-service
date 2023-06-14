package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.repository.SmestajRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmestajService {
    private final SmestajRepository smestajRepository;

    public List<Smestaj> getAll() {
        return smestajRepository.findAll();
    }

    public List<Smestaj> getAllByNaziv(String naziv) {
        return smestajRepository.findByNazivContainsIgnoreCase(naziv);
    }

    public List<Smestaj> getAllByLokacija(String lokacija) {
        return smestajRepository.findByLokacijaContainsIgnoreCase(lokacija);
    }

    public List<Smestaj> getAllByRatingBetterThan(Double rating) {
        return smestajRepository.findByProsecnaOcenaGreaterThanEqual(rating);
    }

    public Smestaj save(Smestaj smestaj) {
        return smestajRepository.save(smestaj);
    }

    public void deleteById(String id) {
        smestajRepository.deleteById(id);
    }

}
