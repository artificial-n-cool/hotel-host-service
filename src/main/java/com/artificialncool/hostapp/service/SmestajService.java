package com.artificialncool.hostapp.service;

import com.artificialncool.hostapp.dto.converter.SmestajConverter;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Promocija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.helpers.Cena;
import com.artificialncool.hostapp.repository.SmestajRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SmestajService {
    private final SmestajRepository smestajRepository;
    private final SmestajConverter smestajConverter;
    private final MongoOperations mongoConnector;

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
        {
            Cena cena = newSmestaj.getBaseCena();
            cena.setId(new ObjectId().toString());
            newSmestaj.setBaseCena(cena);
        }
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

    public Smestaj addPromotion(String smestajId, Promocija promocija) throws EntityNotFoundException{
        Smestaj toPromote = getById(smestajId);
        promocija.setId(new ObjectId().toString());

        /* Mora na ovako retardiran nacin, jer ako se samo odradi get.add
            ne izmeni se sam objekat
         */
        List<Promocija> promotions = toPromote.getPromocije();
        promotions.add(promocija);
        toPromote.setPromocije(promotions);
        return save(toPromote);
    }

    public Smestaj removePromotion(String smestajId, String promocijaId) throws EntityNotFoundException {
        Smestaj toCleanup = getById(smestajId);

        List<Promocija> filteredPromotions
                = toCleanup.getPromocije().stream()
                .filter(promocija -> !promocija.getId().equals(promocijaId))
                .toList();

        toCleanup.setPromocije(filteredPromotions);
        return save(toCleanup);
    }

    public void deleteById(String id) {
        smestajRepository.deleteById(id);
    }

    public Page<Promocija> findPromotionsBySmestajId(String id, Pageable pageable) {
        int pageNo = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        String sortCriteria = pageable.getSort().toString().split(":")[0];
        Smestaj targetSmestaj = smestajRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Nema smestaj"));
        List<Promocija> promocije = targetSmestaj.getPromocije()
                .stream().sorted((Promocija p1, Promocija p2) -> {
                    if (sortCriteria.equals("datumOd")) {
                        return p1.getDatumOd().compareTo(p2.getDatumOd());
                    }
                    else
                        return p1.getDatumDo().compareTo(p2.getDatumDo());
                })
                .toList();
        Page<Promocija> page = PaginationUtils.getPage(promocije, pageNo, pageSize);
        return page;
    }

}
