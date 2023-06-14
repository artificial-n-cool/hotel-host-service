package com.artificialncool.hostapp.dto.converter;

import com.artificialncool.hostapp.dto.model.RezervacijaDTO;
import com.artificialncool.hostapp.model.Rezervacija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.StatusRezervacije;
import org.springframework.stereotype.Component;

@Component
public class RezervacijaConverter {

    public Rezervacija fromDTO(RezervacijaDTO dto) {
        return Rezervacija.builder()
                .id(dto.getId())
                .brojOsoba(dto.getBrojOsoba())
                .datumOd(DateConverter.fromString(dto.getDatumOd()))
                .datumDo(DateConverter.fromString(dto.getDatumDo()))
                .korisnikID(dto.getKorisnikID())
                .statusRezervacije(StatusRezervacije.valueOf(dto.getStatusRezervacije()))
                .build();
    }

    public RezervacijaDTO toDTO(Rezervacija rezervacija) {
        return RezervacijaDTO.builder()
                .id(rezervacija.getId())
                .brojOsoba(rezervacija.getBrojOsoba())
                .datumOd(DateConverter.toString(rezervacija.getDatumOd()))
                .datumDo(DateConverter.toString(rezervacija.getDatumDo()))
                .statusRezervacije(rezervacija.getStatusRezervacije().name())
                .korisnikID(rezervacija.getKorisnikID())
                // TODO: Dodati da se ubaci smesaj kome rezervacija pripada
                .build();
    }

    public RezervacijaDTO toDTOForSmestaj(Rezervacija rezervacija, Smestaj smestaj) {
        return RezervacijaDTO.builder()
                .id(rezervacija.getId())
                .brojOsoba(rezervacija.getBrojOsoba())
                .datumOd(DateConverter.toString(rezervacija.getDatumOd()))
                .datumDo(DateConverter.toString(rezervacija.getDatumDo()))
                .statusRezervacije(rezervacija.getStatusRezervacije().name())
                .korisnikID(rezervacija.getKorisnikID())
                .smestajID(smestaj.getId())
                .build();
    }
}
