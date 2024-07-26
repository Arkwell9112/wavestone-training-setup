package com.wavestone.ansibleTrainingSetup;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SSHHelper {
    public void generateSSHKey(String path) throws IOException, InterruptedException {
        if (new File(path).exists()) return;
        Runtime.getRuntime().exec("ssh-keygen -q -t rsa -b 4096 -N '' -f " + path).waitFor();
    }

    public String loadSSHPublicKeyConfigurationForTerraform(String path) throws IOException {
        String rawKey = Files.readString(Path.of(path));

        String[] keyParts = rawKey.split(" ");

        return keyParts[0] + " " + keyParts[1];
    }
}
