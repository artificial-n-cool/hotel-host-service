package com.artificialncool.hostapp.dto.model;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PromocijaDTO {
    private String id;
    private String datumOd;
    private String datumDo;
    private Double procenat;
    private List<Integer> dani;
    private String smestajId;
}
