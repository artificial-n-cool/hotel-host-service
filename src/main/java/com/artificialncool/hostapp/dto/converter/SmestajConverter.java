package com.artificialncool.hostapp.dto.converter;

import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.helpers.Cena;

public class SmestajConverter {
    /*
        private String id;
    private String naziv;
    private String lokacija;
    private String pogodnosti;
    private String opis;
    private Integer minGostiju;
    private Integer maxGostiju;
    private Double prosecnaOcena;
    private Double baseCena;
    private Double totalnaCena;
    private TipCene tipCene;
    private String vlasnikID;
     */
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
                // TODO: Izracunati finalCena
                .build();
    }
}
