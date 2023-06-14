package com.artificialncool.hostapp.dto.model;

import com.artificialncool.hostapp.model.enums.StatusRezervacije;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RezervacijaDTO {
    private String id;
    private Integer brojOsoba;
    private String datumOd;
    private String datumDo;
    private String statusRezervacije;
    private String korisnikID;
    private String smestajID;
}
