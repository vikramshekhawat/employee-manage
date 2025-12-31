package com.empmanage.config;

import com.empmanage.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TwilioConfig {

    private final SmsService smsService;

    @Bean
    public CommandLineRunner initializeTwilio() {
        return args -> {
            smsService.initializeTwilio();
        };
    }
}


