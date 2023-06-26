package com.artificialncool.hostapp.controller;

import com.artificialncool.hostapp.model.Korisnik;
import com.artificialncool.hostapp.service.KorisnikService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value="/api/host/korisnik")
@RequiredArgsConstructor
public class KorisnikController {

    private final KorisnikService korisnikService;

    private static final Logger logger = LoggerFactory.getLogger(KorisnikController.class);

    @Value ("${spring.application.name}")
    private String applicationName;

    @GetMapping(value = "/all")
    public ResponseEntity<List<Korisnik>> getAll() {
        logger.info("Incoming request at {} for request /all", applicationName);
        return new ResponseEntity<>(korisnikService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/username/{username}")
    public ResponseEntity<Korisnik> getByUsername(@PathVariable String username) {
        try {
            return new ResponseEntity<>(korisnikService.getByUsername(username), HttpStatus.OK);
        }
        catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Korisnik nije pronadjen", ex);
        }
    }

    @PostMapping
    public ResponseEntity<Korisnik> createKorisnik(@RequestBody Korisnik korisnik) {
        // TODO: Notification and Guest service
        return new ResponseEntity<>(korisnikService.saveKorisnik(korisnik), HttpStatus.CREATED);
    }

    @PostMapping(value = "/populate")
    public ResponseEntity<Void> populateDB() {
        korisnikService.initDb();
        return ResponseEntity.ok().build();
    }
}
