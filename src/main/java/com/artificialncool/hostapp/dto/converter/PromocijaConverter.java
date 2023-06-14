package com.artificialncool.hostapp.dto.converter;

import com.artificialncool.hostapp.dto.model.PromocijaDTO;
import com.artificialncool.hostapp.model.Promocija;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class PromocijaConverter {
    public Promocija fromDTO(PromocijaDTO dto) {
        return Promocija.builder()
                .id(dto.getId())
                .datumOd(DateConverter.fromString(dto.getDatumOd()))
                .datumDo(DateConverter.fromString(dto.getDatumDo()))
                .procenat(dto.getProcenat())
                .dani(dto.getDani().stream().map(DayOfWeek::of).toList())
                .build();
    }
}
