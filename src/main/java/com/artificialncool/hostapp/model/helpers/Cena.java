package com.artificialncool.hostapp.model.helpers;

import com.artificialncool.hostapp.model.enums.TipCene;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cena {
    @Id
    private String id;
    private Double cena;
    private TipCene tipCene;
}
