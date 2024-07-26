package com.wavestone.ansibleTrainingSetup.awxAPI;

import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordGenerator {
    private final SecureRandom random;

    public PasswordGenerator() throws NoSuchAlgorithmException {
        this.random = SecureRandom.getInstance("SHA1PRNG");
    }

    public String generatePassword() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
