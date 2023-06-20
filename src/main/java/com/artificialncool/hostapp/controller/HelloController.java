package com.artificialncool.hostapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value="/api/host/hello")
public class HelloController {
    private final RestTemplate restTemplate;

    public HelloController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    @GetMapping
    public ResponseEntity<String> helloWorld() {
        String response
            = restTemplate.getForObject("http://guest-app:8080/api/guest/smestaj/hello", String.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
