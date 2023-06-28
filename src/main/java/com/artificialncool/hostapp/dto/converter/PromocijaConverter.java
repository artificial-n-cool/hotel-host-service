package com.artificialncool.hostapp.dto.converter;

import com.artificialncool.hostapp.dto.model.PromocijaDTO;
import com.artificialncool.hostapp.model.Promocija;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class PromocijaConverter {

    /**
     * Converts a promotion DTO to the Promocija object. Note that the
     * DTO object should contain the ID of the residence that it is being
     * defined for. Also, the start and end dates of the promotion should
     * be strings in the yyyy-MM-dd format to be parsed successfully
     * @param dto DTO object of the promotion. Among other things contains
     *            the ID of the residence it is being defined for and date
     *            strings in the yyyy-MM-dd format
     * @return Parsed Promocija object
     */
    public Promocija fromDTO(PromocijaDTO dto) {
        return Promocija.builder()
                .id(dto.getId())
                .datumOd(DateConverter.fromString(dto.getDatumOd()))
                .datumDo(DateConverter.fromString(dto.getDatumDo()))
                .procenat(dto.getProcenat())
                .dani(dto.getDani().stream().map(DayOfWeek::of).toList())
                .build();
    }

    public PromocijaDTO toDTO(Promocija promocija) {
        return PromocijaDTO.builder()
                .id(promocija.getId())
                .datumOd(promocija.getDatumOd().toString())
                .datumDo(promocija.getDatumDo().toString())
                .procenat(promocija.getProcenat())
                .dani(promocija.getDani().stream().map(DayOfWeek::getValue).toList())
                .build();
    }
}
