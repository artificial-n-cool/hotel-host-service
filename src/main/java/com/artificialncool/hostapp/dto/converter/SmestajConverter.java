package com.artificialncool.hostapp.dto.converter;

import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.helpers.Cena;
import org.springframework.stereotype.Component;

@Component
public class SmestajConverter {
    public Smestaj fromDTO(SmestajDTO dto) {
        return Smestaj.builder()
                .id(dto.getId())
                .naziv(dto.getNaziv())
                .lokacija(dto.getLokacija())
                .opis(dto.getOpis())
                .pogodnosti(dto.getPogodnosti())
                .prosecnaOcena(dto.getProsecnaOcena())
                .minGostiju(dto.getMinGostiju())
                .maxGostiju(dto.getMaxGostiju())
                .baseCena(
                        Cena.builder()
                                .cena(dto.getBaseCena())
                                .tipCene(dto.getTipCene())
                                .build()
                )
                .vlasnikID(dto.getVlasnikID())
                .build();
    }

    public SmestajDTO toDTO(Smestaj smestaj) {
        return SmestajDTO.builder()
                .id(smestaj.getId())
                .naziv(smestaj.getNaziv())
                .lokacija(smestaj.getLokacija())
                .opis(smestaj.getOpis())
                .pogodnosti(smestaj.getPogodnosti())
                .prosecnaOcena(smestaj.getProsecnaOcena())
                .minGostiju(smestaj.getMinGostiju())
                .maxGostiju(smestaj.getMaxGostiju())
                .baseCena(smestaj.getBaseCena().getCena())
                .tipCene(smestaj.getBaseCena().getTipCene())
                .vlasnikID(smestaj.getVlasnikID())
                // TODO: Izracunati finalCena
                .build();
    }
}
