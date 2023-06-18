package com.artificialncool.hostapp.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Promocija {
    private String id;
    private LocalDate datumOd;
    private LocalDate datumDo;
    private Double procenat;
    private List<DayOfWeek> dani;
}
