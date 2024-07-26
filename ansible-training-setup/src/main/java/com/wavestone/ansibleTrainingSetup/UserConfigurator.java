package com.wavestone.ansibleTrainingSetup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Profile("setup")
@Order(1)
public class UserConfigurator implements ApplicationRunner {
    private final InputConfiguration inputConfiguration;

    private final SSHHelper sshHelper;

    private final String userXVM;

    private final String userXFirewall;

    public UserConfigurator(
            ResourceToString resourceToString,
            InputConfiguration inputConfiguration,
            SSHHelper sshHelper,
            @Value("classpath:terraform/user-x-vm.tf") Resource userXVM,
            @Value("classpath:terraform/user-x-firewall.tf") Resource userXFirewall
    ) throws IOException {
        this.inputConfiguration = inputConfiguration;
        this.sshHelper = sshHelper;
        this.userXVM = resourceToString.resourceToString(userXVM);
        this.userXFirewall = resourceToString.resourceToString(userXFirewall);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = args.getOptionValues("path").get(0);

        for (int index = 2; index < inputConfiguration.users().size() + 2; index++) {
            // Generate SSH key for user-vm.
            sshHelper.generateSSHKey(path + String.format("/ansible/user-%s-ssh-key", index));
            //Load the ssh key after generation.
            String sshKey = sshHelper.loadSSHPublicKeyConfigurationForTerraform(path + String.format("/ansible/user-%s-ssh-key.pub", index));

            // Prepare terraform main config for user.
            String currentUserXVM = userXVM
                    .replace("{user-machine-type}", inputConfiguration.userMachineType())
                    .replace("{ssh-key}", sshKey);

            // Prepare terraform user config for user.
            currentUserXVM = currentUserXVM
                    .replace("user-x-subnet-id", String.format("user_%s_subnet", index))
                    .replace("{user-x-subnet-name}", String.format("user-%s-subnet", index))
                    .replace("{user-x-subnet-range}", String.format("10.1.%s.0/24", index))
                    .replace("user-x-ip-id", String.format("user_%s_ip", index))
                    .replace("{user-x-ip-name}", String.format("user-%s-ip", index))
                    .replace("user-x-vm-id", String.format("user_%s_vm", index))
                    .replace("{user-x-vm-name}", String.format("user-%s-vm", index))
                    .replace("{user-x-owner}", String.format("user-%s", index))
                    .replace("user-x-vm-public-ip-output-id", String.format("user_%s_vm_public_ip", index))
                    .replace("user-x-vm-private-ip-output-id", String.format("user_%s_vm_private_ip", index));

            String currentUserXFirewall = userXFirewall
                    .replace("user-x-ingress-internal-id", String.format("user_%s_ingress_internal", index))
                    .replace("{user-x-ingress-internal-name}", String.format("user-%s-ingress-internal", index))
                    .replace("{user-x-ingress-destination-range}", String.format("10.1.%s.0/24", index))
                    .replace("user-x-ingress-external-id", String.format("user_%s_ingress_external", index))
                    .replace("{user-x-ingress-external-name}", String.format("user-%s-ingress-external", index));

            // Write terraform user config for user.
            Files.writeString(Path.of(path + "/terraform" + String.format("/user-%s-vm.tf", index)), currentUserXVM);
            Files.writeString(Path.of(path + "/terraform" + String.format("/user-%s-firewall.tf", index)), currentUserXFirewall);
        }
    }
}
