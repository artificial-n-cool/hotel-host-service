package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.dto.converter.RezervacijaConverter;
import com.artificialncool.hostapp.dto.model.RezervacijaExtendedDTO;
import com.artificialncool.hostapp.model.Promocija;
import com.artificialncool.hostapp.model.Rezervacija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.StatusRezervacije;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RezervacijaService {

    final SmestajService smestajService;
    final RezervacijaConverter rezervacijaConverter;

    public List<Rezervacija> findAll() {
        return smestajService.getAll()
                .stream()
                .flatMap(smestaj -> smestaj.getRezervacije().stream())
                .toList();
    }

    public List<Rezervacija> findAllBySmestaj(String smestajId) throws EntityNotFoundException {
        return smestajService.getById(smestajId).getRezervacije();
    }

    public List<RezervacijaExtendedDTO> findAllByGuest(String korisnikId) throws EntityNotFoundException {
        return smestajService.getAll()
                .stream()
                .flatMap(smestaj
                        -> smestaj.getRezervacije()
                            .stream()
                            .map(r -> new RezervacijaExtendedDTO(
                                    rezervacijaConverter.toDTO(r), smestaj.getNaziv()
                            )))
                .filter(r -> r.getKorisnikID().equals(korisnikId))
                .toList();
    }

    public Page<RezervacijaExtendedDTO> findAllByGuest(String korisnikId, Pageable pageable) throws EntityNotFoundException {
        List<RezervacijaExtendedDTO> targetReservations = new ArrayList<>(findAllByGuest(korisnikId));
        String sortCriterium = pageable.getSort().toString().split(":")[0];
        if (sortCriterium.equals("datumOd"))
            targetReservations.sort(Comparator.comparing(RezervacijaExtendedDTO::getDatumOd));
        else if (sortCriterium.equals("datumDo"))
            targetReservations.sort(Comparator.comparing(RezervacijaExtendedDTO::getDatumDo));
        else
            targetReservations.sort(Comparator.comparing(RezervacijaExtendedDTO::getSmestajNaziv));
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        return PaginationUtils.getPage(targetReservations, pageNumber, pageSize);
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

    public List<Rezervacija> rejectAllOverlaping(String id, String smestajId) {
        Smestaj smestaj = smestajService.getById(smestajId);
        Rezervacija prihvacna = findByIdAndSmestaj(id, smestajId);
        List<Rezervacija> odbijene = smestaj.getRezervacije()
                        .stream()
                        .filter(r -> !r.getId().equals(id) && checkIfOverlap(r, prihvacna))
                        .toList();

        smestaj.setRezervacije(
                smestaj.getRezervacije()
                        .stream()
                        .peek(r -> {
                            if (!r.getId().equals(id) && checkIfOverlap(r, prihvacna))
                                r.setStatusRezervacije(StatusRezervacije.ODBIJENO);
                        })
                        .toList()
        );

        smestajService.save(smestaj);
        return odbijene;
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

        if (rezervacija.getId() == null || rezervacija.getId().equals(""))
            rezervacija.setId(new ObjectId().toString());
        List<Rezervacija> sve = smestaj.getRezervacije();
        sve.add(rezervacija);
        smestaj.setRezervacije(sve);
        return smestajService.save(smestaj);
    }

    public Page<Rezervacija> getAllUnavailabilitiesForSmestaj(String smestajId, Pageable pageable)
            throws NoSuchElementException {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        String sortCriteria = pageable.getSort().toString().split(":")[0];
        Smestaj targetSmestaj = smestajService.getById(smestajId);
        List<Rezervacija> rezervacije = targetSmestaj.getRezervacije()
                .stream()
                .filter((rezervacija ->
                    rezervacija.getKorisnikID().equals(targetSmestaj.getVlasnikID())
                ))
                .sorted((Rezervacija p1, Rezervacija p2) -> {
                    if (sortCriteria.equals("datumOd")) {
                        return p1.getDatumOd().compareTo(p2.getDatumOd());
                    }
                    else
                        return p1.getDatumDo().compareTo(p2.getDatumDo());
                })
                .toList();
        Page<Rezervacija> page = PaginationUtils.getPage(rezervacije, pageNumber, pageSize);
        return page;
    }

    public void removeRezervacijaForSmestaj(String rezervacijaId, String smestajId) {
        Smestaj targetSmestaj = smestajService.getById(smestajId);
        List<Rezervacija> rezervacije = targetSmestaj.getRezervacije().stream()
                .filter(r -> !r.getId().equals(rezervacijaId))
                .toList();

        targetSmestaj.setRezervacije(rezervacije);
    }
}
