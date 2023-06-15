package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.model.Rezervacija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.StatusRezervacije;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RezervacijaService {

    final SmestajService smestajService;

    List<Rezervacija> findAll() {
        return smestajService.getAll()
                .stream()
                .flatMap(smestaj -> smestaj.getRezervacije().stream())
                .toList();
    }

    List<Rezervacija> findAllBySmestaj(String smestajId) throws EntityNotFoundException {
        return smestajService.getById(smestajId).getRezervacije();
    }

    Rezervacija findByIdAndSmestaj(String id, String smestajId) throws EntityNotFoundException {
        Smestaj smestaj = smestajService.getById(smestajId);
        Rezervacija rezervacija
                = smestaj.getRezervacije()
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst().orElseThrow(EntityNotFoundException::new);
        return rezervacija;
    }

    Rezervacija setReservationStatus(String id, String smestajId, StatusRezervacije status) {
        Smestaj smestaj = smestajService.getById(smestajId);
        smestaj.setRezervacije(
                smestaj.getRezervacije()
                        .stream()
                        .peek(r -> {
                            if (r.getId().equals(id)) {
                                r.setStatusRezervacije(status);
                            }
                        })
                        .toList()
        );
        return findByIdAndSmestaj(id, smestajId);
    }

    boolean checkIfOverlap(Rezervacija r1, Rezervacija r2) {
        boolean overlap = r1.getDatumOd().isBefore(r2.getDatumDo())
                && r1.getDatumDo().isAfter(r2.getDatumOd());

        return overlap;
    }
}
