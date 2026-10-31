package com.example.custproc;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustprocApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustprocApplication.class, args);
    }

    @Bean
    Clock clock(@Value("${custproc.report.time-zone:UTC}") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
