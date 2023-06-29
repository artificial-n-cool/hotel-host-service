package com.artificialncool.hostapp.controller;

import com.artificialncool.hostapp.dto.converter.RezervacijaConverter;
import com.artificialncool.hostapp.dto.converter.SmestajConverter;
import com.artificialncool.hostapp.dto.model.RezervacijaDTO;
import com.artificialncool.hostapp.dto.model.SmestajDTO;
import com.artificialncool.hostapp.model.Rezervacija;
import com.artificialncool.hostapp.model.Smestaj;
import com.artificialncool.hostapp.model.enums.StatusRezervacije;
import com.artificialncool.hostapp.service.RezervacijaService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value="/api/host/rezervacije")
public class RezervacijaController {

    final RezervacijaService rezervacijaService;
    final RezervacijaConverter rezervacijaConverter;
    final SmestajConverter smestajConverter;
    final RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    private static final Logger logger = LoggerFactory.getLogger(KorisnikController.class);


    public RezervacijaController(RezervacijaService rezervacijaService, RezervacijaConverter rezervacijaConverter, SmestajConverter smestajConverter, RestTemplateBuilder builder) {
        this.rezervacijaService = rezervacijaService;
        this.rezervacijaConverter = rezervacijaConverter;
        this.smestajConverter = smestajConverter;
        this.restTemplate = builder.build();
    }

    @GetMapping(value = "/{smestajId}")
    public ResponseEntity<List<RezervacijaDTO>>
    getForSmestaj(@PathVariable String smestajId) {
        logger.info("Incoming GET request at {} for request /rezervacije", applicationName);

        List<RezervacijaDTO> rezervacije
                = rezervacijaService.findAllBySmestaj(smestajId)
                .stream()
                .map(rez -> rezervacijaConverter.toDTOForSmestaj(rez, smestajId))
                .toList();

        return new ResponseEntity<>(rezervacije, HttpStatus.OK);
    }

    @GetMapping(value = "/nove/{smestajId}")
    public ResponseEntity<List<RezervacijaDTO>>
    getNewForSmestaj(@PathVariable String smestajId) {
        logger.info("Incoming GET request at {} for request /rezervacije/nove", applicationName);
        List<RezervacijaDTO> rezervacije
                = rezervacijaService.findAllNewBySmestaj(smestajId)
                .stream()
                .map(rez -> rezervacijaConverter.toDTOForSmestaj(rez, smestajId))
                .toList();

        return new ResponseEntity<>(rezervacije, HttpStatus.OK);
    }


    /**
     * It is important to notice that the reservation DTO has to contain the ID of
     * the residence that is to be reserved and the start and end dates that have
     * to be strings in yyyy-MM-dd format
     *
     * @param rezervacijaDTO DTO that should contain start and end dates as strings
     *                       in yyyy-MM-dd format, and the ID of the residence
     * @return DTO of the edited residence
     */
    @PostMapping
    public ResponseEntity<SmestajDTO>
    createNewForSmestaj(@RequestBody RezervacijaDTO rezervacijaDTO) {
        logger.info("Incoming POST request at {} for request /rezervacije", applicationName);
        Rezervacija rezervacija = rezervacijaConverter.fromDTO(rezervacijaDTO);

        try {
            Smestaj updated
                    = rezervacijaService.addReservationForSmestaj(
                    rezervacija, rezervacijaDTO.getSmestajID()
            );
            // TODO: Send to Guest service
            return new ResponseEntity<>(smestajConverter.toDTO(updated), HttpStatus.OK);

        }
        catch (IllegalArgumentException e1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ovaj termin je vec zauzet", e1);
        }
        catch (EntityNotFoundException e2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema takav smestaj", e2);
        }
    }

    /**
     * This creates a new reservation that is automatically accepted. Due to this
     * other reservations cannot be made in the given interval. Please read the
     * requirements for the passed params for the reserveNew function
     *
     * @param unavailabilityDTO DTO containing start and end dates as yyyy-MM-dd
     *                          strings, and the ID of the residence
     * @return Edited residence DTO
     */
    @PostMapping(value = "/set-unavailable")
    public ResponseEntity<SmestajDTO>
    createNewUnavailability(@RequestBody RezervacijaDTO unavailabilityDTO) {
        logger.info("Incoming POST request at {} for request /rezervacije/set-unavailable", applicationName);
        Rezervacija unavailability = rezervacijaConverter.fromDTO(unavailabilityDTO);
        unavailability.setStatusRezervacije(StatusRezervacije.PRIHVACENO);
        try {
            Smestaj updated
                    = rezervacijaService.addReservationForSmestaj(
                    unavailability, unavailabilityDTO.getSmestajID()
            );
            // TODO: Send update to Guest app
            unavailabilityDTO.setStatusRezervacije(StatusRezervacije.PRIHVACENO.name());
            try {
                restTemplate.postForEntity(
                        "http://guest-app-service:8080/api/guest/rezervacija/addRezervacija",
                        unavailabilityDTO,
                        Void.class
                );
            }
            catch (RestClientException ex) {
                ex.printStackTrace();
                System.out.println("Nebitno");
            }
            return new ResponseEntity<>(smestajConverter.toDTO(updated), HttpStatus.OK);

        }
        catch (IllegalArgumentException e1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ovaj termin je vec zauzet", e1);
        }
        catch (EntityNotFoundException e2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema takav smestaj", e2);
        }
    }

    @GetMapping(value = "get-unavailable/{smestajId}")
    public Page<RezervacijaDTO> getAllUnavailabilitiesForSmestaj(@PathVariable String smestajId, @PageableDefault Pageable pageable) {
        try {
            Page<Rezervacija> rezervacije = rezervacijaService.getAllUnavailabilitiesForSmestaj(smestajId, pageable);
            return rezervacije.map(rezervacijaConverter::toDTO);
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema takav smestaj", e);
        }
    }



    @PutMapping(value = "/accept/{rezId}/{smestajId}")
    public ResponseEntity<RezervacijaDTO>
    acceptReservation(@PathVariable String rezId, @PathVariable String smestajId) {
        // TODO: Dodati mogucnost za automatski accept/reject
        logger.info("Incoming PUT request at {} for request /accept", applicationName);
        Rezervacija accepted
                = rezervacijaService.setReservationStatus(rezId, smestajId, StatusRezervacije.PRIHVACENO);
        // TODO: send update to Guest app
        try {
            restTemplate.exchange(
                    String.format("http://guest-app-service:8080/api/guest/rezervacija/setStatusRezervacije/%s/%s/%s", rezId, smestajId, StatusRezervacije.PRIHVACENO),
                    HttpMethod.PUT,
                    null,
                    Void.class
            );
        }
        catch (RestClientException ex) {
            ex.printStackTrace();
            System.out.println("Nebitno");
        }

        List<Rezervacija> odbijene = rezervacijaService.rejectAllOverlaping(rezId, smestajId);
        for (Rezervacija odbijena : odbijene) {
            try {
                restTemplate.exchange(
                        String.format("http://guest-app-service:8080/api/guest/rezervacija/setStatusRezervacije/%s/%s/%s", odbijena.getId(), smestajId, StatusRezervacije.ODBIJENO),
                        HttpMethod.PUT,
                        null,
                        Void.class
                );
            }
            catch (RestClientException ex) {
                ex.printStackTrace();
                System.out.println("Nebitno");
            }
        }
        // TODO: Send update to Guest app
        return new ResponseEntity<>(
                rezervacijaConverter.toDTOForSmestaj(accepted, smestajId), HttpStatus.OK
        );
    }

    @PutMapping(value = "/reject/{rezId}/{smestajId}")
    public ResponseEntity<RezervacijaDTO>
    rejectReservation(@PathVariable String rezId, @PathVariable String smestajId) {
        logger.info("Incoming PUT request at {} for request /rezervacije/reject", applicationName);
        Rezervacija rejected
                = rezervacijaService.setReservationStatus(rezId, smestajId, StatusRezervacije.ODBIJENO);
        try {
            restTemplate.exchange(
                    String.format("http://guest-app-service:8080/api/guest/rezervacija/setStatusRezervacije/%s/%s/%s", rezId, smestajId, StatusRezervacije.ODBIJENO),
                    HttpMethod.PUT,
                    null,
                    Void.class
            );
        }
        catch (RestClientException ex) {
            ex.printStackTrace();
            System.out.println("Nebitno");
        }
        return new ResponseEntity<>(
                rezervacijaConverter.toDTOForSmestaj(rejected, smestajId), HttpStatus.OK
        );
    }

    @PutMapping(value = "/cancel/{rezId}/{smestajId}")
    public ResponseEntity<RezervacijaDTO>
    cancelReservation(@PathVariable String rezId, @PathVariable String smestajId) {
        Rezervacija cancelled
                = rezervacijaService.setReservationStatus(rezId, smestajId, StatusRezervacije.OTKAZANO);
        // TODO: Send update to Guest app
        return new ResponseEntity<>(
                rezervacijaConverter.toDTOForSmestaj(cancelled, smestajId), HttpStatus.OK
        );
    }
}
