package com.artificialncool.hostapp.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document("promocije")
public class Promocija {
    @Id
    private Long ID;
    private LocalDate datumOd;
    private LocalDate datumDo;
    private Double procenat;
    private List<DayOfWeek> dani;

    @DocumentReference
    private Smestaj smestaj;
}
