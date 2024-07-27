package com.wavestone.ansibleTrainingSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavestone.ansibleTrainingSetup.awxAPI.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Profile("configure")
@Order(0)
public class AWXConfigurator implements ApplicationRunner {
    private final ObjectMapper objectMapper;

    private final PasswordGenerator passwordGenerator;

    private final InputConfiguration inputConfiguration;

    private final AWXHelper awxHelper;

    private final TerraformHelper terraformHelper;

    public AWXConfigurator(ObjectMapper objectMapper, PasswordGenerator passwordGenerator, InputConfiguration inputConfiguration, AWXHelper awxHelper, TerraformHelper terraformHelper) {
        this.objectMapper = objectMapper;
        this.passwordGenerator = passwordGenerator;
        this.inputConfiguration = inputConfiguration;
        this.awxHelper = awxHelper;
        this.terraformHelper = terraformHelper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = args.getOptionValues("path").get(0);

        String awxIP = args.getOptionValues("awxIP").get(0);
        String awxPort = args.getOptionValues("awxPort").get(0);

        String awxHost = "http://" + awxIP + ":" + awxPort;

        for (int index = 2; index < inputConfiguration.users().size() + 2; index++) {
            InputConfiguration.User userInput = inputConfiguration.users().get(index - 2);

            // Create user organization.
            ElementList.ElementResponse organization = awxHelper.createOrganization(
                    userInput.username() + " - Organization",
                    "Organization for " + userInput.username() + " " + userInput.role() + "."
            );

            // Create user account inside his organization.
            String password = passwordGenerator.generatePassword();

            boolean forceUpdate = !new File(path + "/users/" + userInput.username() + ".json").exists();

            awxHelper.createUserInOrganization(
                    organization.id(),
                    userInput.username(),
                    userInput.role() == InputConfiguration.User.Role.TRAINER,
                    password,
                    forceUpdate
            );

            // Give user administration rights on his organization.

            // Create SSH credential for user VMs.
            awxHelper.createSSHMachineCredential(
                    userInput.username() + " - SSH",
                    userInput.username() + " VMs ssh credential.",
                    organization.id(),
                    Files.readString(Path.of(path + String.format("/ansible/user-%s-ssh-key", index)))
            );

            // Create GCP Service Account credential for user dynamic inventory.

            // Create Git credential for user repository.

            // Get user VMs information from terraform output.
            String firstVMPrivateIP = terraformHelper.getUserVMPrivateIPFirst(index);
            String firstVMPublicIP = terraformHelper.getUserVMPublicIPFirst(index);
            String secondVMPrivateIP = terraformHelper.getUserVMPrivateIPSecond(index);
            String secondVMPublicIP = terraformHelper.getUserVMPublicIPSecond(index);

            // Write user information in users directory.
            UserInfo userInfo = new UserInfo(
                    index,
                    userInput.username(),
                    userInput.role(),
                    password,
                    firstVMPrivateIP,
                    firstVMPublicIP,
                    secondVMPrivateIP,
                    secondVMPublicIP
            );

            if (forceUpdate)
                Files.writeString(Path.of(path + "/users/" + userInput.username() + ".json"), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userInfo));
        }

        // Write general information.
        GeneralInfo generalInfo = new GeneralInfo(
                awxIP,
                awxPort,
                awxHost
        );

        Files.writeString(Path.of(path + "/output.json"), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(generalInfo));
    }
}
