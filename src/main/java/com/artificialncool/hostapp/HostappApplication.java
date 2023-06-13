package com.artificialncool.hostapp;

import com.artificialncool.hostapp.model.Korisnik;
import com.artificialncool.hostapp.model.enums.KorisnickaUloga;
import com.artificialncool.hostapp.repository.KorisnikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication  (exclude={DataSourceAutoConfiguration.class})
@EnableMongoRepositories
public class HostappApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostappApplication.class, args);
    }
}
