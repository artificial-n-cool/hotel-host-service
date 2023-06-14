package com.artificialncool.hostapp.dto.model;

import com.artificialncool.hostapp.model.enums.TipCene;
import com.artificialncool.hostapp.model.helpers.Cena;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SmestajDTO {
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
}
