package com.artificialncool.hostapp.controller;

import com.artificialncool.hostapp.dto.converter.PromocijaConverter;
import com.artificialncool.hostapp.dto.model.PromocijaDTO;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Promocija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.service.SmestajService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Value("${spring.application.name}")
    private String applicationName;

    private static final Logger logger = LoggerFactory.getLogger(KorisnikController.class);
    public SmestajController(SmestajService smestajService, PromocijaConverter promocijaConverter, RestTemplateBuilder builder) {
        this.smestajService = smestajService;
        this.promocijaConverter = promocijaConverter;
        this.restTemplate = builder.build();
    }
    @PostMapping
    public ResponseEntity<SmestajDTO> create(@RequestBody SmestajDTO newSmestajDTO) {
        logger.info("Incoming POST request at {} for request /smestaj", applicationName);
        SmestajDTO saved = smestajService.save(newSmestajDTO);
        Thread syncThread = new Thread(() -> {
            try {
                restTemplate.postForEntity(
                        "http://guest-app-service:8080/api/guest/smestaj/addSmestaj",
                        newSmestajDTO,
                        Void.class
                );
                restTemplate.postForEntity(
                        "http://auth-app-service:8080/api/auth/smestaj/addSmestaj",
                        newSmestajDTO,
                        Void.class
                );
            }
            catch (RestClientException ex) {
                ex.printStackTrace();
                System.out.println("Nebitno");
            }
        });
        syncThread.start();


        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SmestajDTO> readOne(@PathVariable String id) {
        logger.info("Incoming GET request at {} for request /smestaj/ID", applicationName);
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
        logger.info("Incoming GET request at {} for request /smestaj", applicationName);
        return new ResponseEntity<>(
                smestajService.getAll()
                        .stream().map(smestajService::toDTO).toList(),
                HttpStatus.OK
        );
    }

    @PutMapping
    public ResponseEntity<SmestajDTO> update(@RequestBody SmestajDTO updatedDTO) {
        logger.info("Incoming PUT request at {} for request /smestaj", applicationName);
        try {
            Smestaj updated = smestajService.fromDTO(updatedDTO);
            SmestajDTO savedDTO = smestajService.toDTO(smestajService.update(updated));
            try {
                restTemplate.exchange(
                        "http://guest-app-service:8080/api/guest/smestaj/updateSmestaj",
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

    @GetMapping(value = "/promocije/{smestajId}")
    public Page<PromocijaDTO> getAllPromocije(@PathVariable String smestajId, @PageableDefault Pageable pageable) {
        logger.info("Incoming GET request at {} for request /smestaj/promocije/ID", applicationName);
        List<Smestaj> smestaji = this.smestajService.getAll();
        Page<Promocija> promocije = this.smestajService.findPromotionsBySmestajId(smestajId, pageable);
        return this.smestajService
                .findPromotionsBySmestajId(smestajId, pageable)
                .map(promocijaConverter::toDTO);
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
        logger.info("Incoming PUT request at {} for request /smestaj/promocija", applicationName);
        Promocija promocija = promocijaConverter.fromDTO(promocijaDTO);
        Smestaj updated = smestajService.addPromotion(promocijaDTO.getSmestajId(), promocija);
        // TODO: Send update to Guest app
        return new ResponseEntity<>(
            smestajService.toDTO(updated), HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/promocija")
    public ResponseEntity<SmestajDTO> removePromocija(@RequestBody PromocijaDTO promocijaDTO) {
        logger.info("Incoming DELETE request at {} for request /smestaj/promocija", applicationName);
        Promocija promocija = promocijaConverter.fromDTO(promocijaDTO);
        Smestaj updated = smestajService.removePromotion(promocijaDTO.getSmestajId(), promocija.getId());
        // TODO: Send update to Guest app
        return new ResponseEntity<>(
                smestajService.toDTO(updated), HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable String id) {
        logger.info("Incoming DELETE request at {} for request /smestaj", applicationName);
        smestajService.deleteById(id);
        try {
            restTemplate.delete("http://guest-app-service:8080/api/guest/deleteSmestaj/" + id);
        }
        catch (RestClientException ex) {
            ex.printStackTrace();
            System.out.println("Nebitno");
        }
        // TODO: Send update to Guest app
        return ResponseEntity.ok().build();
    }
}
