package com.artificialncool.hostapp.controller;

import com.artificialncool.hostapp.dto.converter.PromocijaConverter;
import com.artificialncool.hostapp.dto.model.PromocijaDTO;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Promocija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.service.SmestajService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value="/api/host/smestaj")
public class SmestajController {
    private final SmestajService smestajService;
    private final PromocijaConverter promocijaConverter;
    private final RestTemplate restTemplate;

    public SmestajController(SmestajService smestajService, PromocijaConverter promocijaConverter, RestTemplateBuilder builder) {
        this.smestajService = smestajService;
        this.promocijaConverter = promocijaConverter;
        this.restTemplate = builder.build();
    }
    @PostMapping
    public ResponseEntity<SmestajDTO> create(@RequestBody SmestajDTO newSmestajDTO) {
        SmestajDTO saved = smestajService.save(newSmestajDTO);
        try {
            restTemplate.postForEntity(
                    "http://guest-app:8080/api/guest/smestaj/addSmestaj",
                    newSmestajDTO,
                    Void.class
            );
        }
        catch (RestClientException ex) {
            ex.printStackTrace();
            System.out.println("Nebitno");
        }

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
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
            SmestajDTO savedDTO = smestajService.toDTO(smestajService.update(updated));
            try {
                restTemplate.exchange(
                        "http://guest-app:8080/api/guest/smestaj/updateSmestaj",
                        HttpMethod.PUT,
                        new HttpEntity<>(savedDTO, null),
                        Void.class
                );
            }
            catch (RestClientException ex) {
                ex.printStackTrace();
                System.out.println("Nebitno");
            }

            return new ResponseEntity<>(savedDTO, HttpStatus.OK);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema smestaj sa tim ID", e);
        }
    }

    /**
     * Adds a promotion to the specified residence. Note that the ID of the
     * specified residence should be stored within the DTO object.
     * Also, the dates of the start and end of the promotion should be
     * strings in the yyyy-MM-dd format to be parsed successfully
     *
     * @param promocijaDTO DTO for a promotion. Should store the ID of the
     *                     residence in question, and promotion start and end
     *                     dates in the yyyy-MM-dd format.
     * @return Updated residence DTO
     */
    @PutMapping(value = "/promocija")
    public ResponseEntity<SmestajDTO> addPromocija(@RequestBody PromocijaDTO promocijaDTO) {
        Promocija promocija = promocijaConverter.fromDTO(promocijaDTO);
        Smestaj updated = smestajService.addPromotion(promocijaDTO.getSmestajId(), promocija);
        // TODO: Send update to Guest app
        return new ResponseEntity<>(
            smestajService.toDTO(updated), HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/promocija")
    public ResponseEntity<SmestajDTO> removePromocija(@RequestBody PromocijaDTO promocijaDTO) {
        Promocija promocija = promocijaConverter.fromDTO(promocijaDTO);
        Smestaj updated = smestajService.removePromotion(promocijaDTO.getSmestajId(), promocija.getId());
        // TODO: Send update to Guest app
        return new ResponseEntity<>(
                smestajService.toDTO(updated), HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable String id) {
        smestajService.deleteById(id);
        try {
            restTemplate.delete("http://guest-api:8080/api/guest/deleteSmestaj/" + id);
        }
        catch (RestClientException ex) {
            ex.printStackTrace();
            System.out.println("Nebitno");
        }
        // TODO: Send update to Guest app
        return ResponseEntity.ok().build();
    }
}
