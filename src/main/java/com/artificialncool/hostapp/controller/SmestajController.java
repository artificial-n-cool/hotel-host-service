package com.artificialncool.hostapp.controller;

import com.artificialncool.hostapp.dto.converter.PromocijaConverter;
import com.artificialncool.hostapp.dto.model.PromocijaDTO;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Promocija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.service.SmestajService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value="/api/host/smestaj")
@RequiredArgsConstructor
public class SmestajController {
    private final SmestajService smestajService;
    private final PromocijaConverter promocijaConverter;

    @PostMapping
    public ResponseEntity<SmestajDTO> create(@RequestBody SmestajDTO newSmestajDTO) {
        return new ResponseEntity<>(smestajService.save(newSmestajDTO), HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SmestajDTO> readOne(@PathVariable String id) {
        try {
            return new ResponseEntity<>(
                    smestajService.toDTO(smestajService.getById(id)),
                    HttpStatus.OK
            );
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema smestaj sa tim ID", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<SmestajDTO>> readAll() {
        return new ResponseEntity<>(
                smestajService.getAll()
                        .stream().map(smestajService::toDTO).toList(),
                HttpStatus.OK
        );
    }

    @PutMapping
    public ResponseEntity<SmestajDTO> update(@RequestBody SmestajDTO updatedDTO) {
        try {
            Smestaj updated = smestajService.fromDTO(updatedDTO);
            return new ResponseEntity<>(smestajService.toDTO(
                    smestajService.update(updated)
            ), HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema smestaj sa tim ID", e);
        }
    }

    @PutMapping(value = "/promocija/{smestajId}")
    public ResponseEntity<SmestajDTO> addPromocija(@RequestBody PromocijaDTO promocijaDTO) {
        Promocija promocija = promocijaConverter.fromDTO(promocijaDTO);
        Smestaj updated = smestajService.addPromotion(promocijaDTO.getSmestajId(), promocija);
        return new ResponseEntity<>(
            smestajService.toDTO(updated), HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable String id) {
        smestajService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
