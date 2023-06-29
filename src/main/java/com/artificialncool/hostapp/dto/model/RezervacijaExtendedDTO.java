package com.artificialncool.hostapp.dto.model;

import com.artificialncool.hostapp.model.Rezervacija;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RezervacijaExtendedDTO {
    private String id;
    private Integer brojOsoba;
    private String datumOd;
    private String datumDo;
    private String statusRezervacije;
    private String korisnikID;
    private String smestajID;
    private String smestajNaziv;

    public RezervacijaExtendedDTO(RezervacijaDTO r, String smestajNaziv) {
        this.id = r.getId();
        this.brojOsoba = r.getBrojOsoba();
        this.datumOd = r.getDatumOd();
        this.datumDo = r.getDatumDo();
        this.statusRezervacije = r.getStatusRezervacije();
        this.korisnikID = r.getKorisnikID();
        this.smestajID = r.getSmestajID();
        this.smestajNaziv = smestajNaziv;
    }
}
