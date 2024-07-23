package com.wavestone.ansibleTrainingSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class Configurator {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ConfigurationInput configurationInputs(ApplicationArguments applicationArguments) throws IOException {
        return objectMapper().readValue(new File(applicationArguments.getOptionValues("input").get(0)), ConfigurationInput.class);
    }

    @Bean
    public SSHKeyConfiguration sshKeyConfiguration(ApplicationArguments applicationArguments) throws IOException {
        String rawKey = Files.readString(Path.of(applicationArguments.getOptionValues("sshPath").get(0)));

        String[] keyParts = rawKey.split(" ");
        String key = keyParts[0] + " " + keyParts[1];

        return new SSHKeyConfiguration(key);
    }
}
