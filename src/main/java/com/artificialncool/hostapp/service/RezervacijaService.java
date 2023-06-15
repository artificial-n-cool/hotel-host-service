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

    public List<Rezervacija> findAll() {
        return smestajService.getAll()
                .stream()
                .flatMap(smestaj -> smestaj.getRezervacije().stream())
                .toList();
    }

    public List<Rezervacija> findAllBySmestaj(String smestajId) throws EntityNotFoundException {
        return smestajService.getById(smestajId).getRezervacije();
    }

    public List<Rezervacija> findAllNewBySmestaj(String smestajId) throws EntityNotFoundException {
        return smestajService.getById(smestajId)
                .getRezervacije()
                .stream()
                .filter(rez -> rez.getStatusRezervacije().equals(StatusRezervacije.U_OBRADI))
                .toList();
    }

    public Rezervacija findByIdAndSmestaj(String id, String smestajId) throws EntityNotFoundException {
        Smestaj smestaj = smestajService.getById(smestajId);
        Rezervacija rezervacija
                = smestaj.getRezervacije()
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst().orElseThrow(EntityNotFoundException::new);
        return rezervacija;
    }

    public Rezervacija setReservationStatus(String id, String smestajId, StatusRezervacije status) {
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
        smestajService.save(smestaj);
        return findByIdAndSmestaj(id, smestajId);
    }

    public Smestaj rejectAllOverlaping(String id, String smestajId) {
        Smestaj smestaj = smestajService.getById(id);
        Rezervacija prihvacna = findByIdAndSmestaj(id, smestajId);
        smestaj.setRezervacije(
                smestaj.getRezervacije()
                        .stream()
                        .peek(r -> {
                            if (!r.getId().equals(id) && checkIfOverlap(r, prihvacna))
                                r.setStatusRezervacije(StatusRezervacije.ODBIJENO);
                        })
                        .toList()
        );

        return smestajService.save(smestaj);
    }

    public boolean checkIfOverlap(Rezervacija r1, Rezervacija r2) {
        boolean overlap = r1.getDatumOd().isBefore(r2.getDatumDo())
                && r1.getDatumDo().isAfter(r2.getDatumOd());

        return overlap;
    }

    public boolean canReserve(Rezervacija r, Smestaj s) {
        boolean overlaps = s.getRezervacije()
                .stream()
                .anyMatch(rez ->
                        rez.getStatusRezervacije().equals(StatusRezervacije.PRIHVACENO)
                                && checkIfOverlap(rez, r)
                );

        return !overlaps;
    }

    public Smestaj addReservationForSmestaj
            (Rezervacija rezervacija, String smestajId) throws EntityNotFoundException{
        Smestaj smestaj = smestajService.getById(smestajId);

        if (!canReserve(rezervacija, smestaj))
            throw new IllegalArgumentException("Rezervacija se preklapa");

        List<Rezervacija> sve = smestaj.getRezervacije();
        sve.add(rezervacija);
        smestaj.setRezervacije(sve);
        return smestajService.save(smestaj);
    }
}
