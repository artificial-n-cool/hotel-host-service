package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.dto.converter.SmestajConverter;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.repository.SmestajRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmestajService {
    private final SmestajRepository smestajRepository;
    private final SmestajConverter smestajConverter;

    public Smestaj fromDTO(SmestajDTO dto) {
        return smestajConverter.fromDTO(dto);
    }

    public SmestajDTO toDTO(Smestaj smestaj) {
        return smestajConverter.toDTO(smestaj);
    }

    public Smestaj getById(String id) throws EntityNotFoundException{
        return smestajRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such smestaj"));
    }

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

    public SmestajDTO save(SmestajDTO newSmestajDTO) {
        Smestaj newSmestaj = smestajConverter.fromDTO(newSmestajDTO);
        newSmestaj = save(newSmestaj);
        return smestajConverter.toDTO(newSmestaj);
    }

    public Smestaj save(Smestaj smestaj) {
        return smestajRepository.save(smestaj);
    }

    public Smestaj copy(Smestaj old, Smestaj updated) {
        old.setNaziv(updated.getNaziv());
        old.setLokacija(updated.getLokacija());
        old.setPogodnosti(updated.getPogodnosti());
        old.setOpis(updated.getOpis());
        old.setMinGostiju(updated.getMinGostiju());
        old.setMaxGostiju(updated.getMaxGostiju());
        old.setBaseCena(updated.getBaseCena());

        return old;
    }

    public Smestaj update(Smestaj updated) throws EntityNotFoundException{
        Smestaj old = getById(updated.getId());

        old = copy(old, updated);

        return smestajRepository.save(old);
    }

    public void deleteById(String id) {
        smestajRepository.deleteById(id);
    }

}
