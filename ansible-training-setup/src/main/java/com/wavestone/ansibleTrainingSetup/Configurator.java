package com.wavestone.ansibleTrainingSetup;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class Configurator {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public InputConfiguration configurationInputs(ApplicationArguments applicationArguments) throws IOException {
        return objectMapper().readValue(new File(applicationArguments.getOptionValues("input").get(0)), InputConfiguration.class);
    }
}
