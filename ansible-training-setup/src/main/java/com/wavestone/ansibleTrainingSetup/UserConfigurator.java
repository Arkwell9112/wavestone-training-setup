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
    private final ConfigurationInput configurationInput;

    private final String userXVM;

    private final String userXFirewall;

    public UserConfigurator(
            ResourceToString resourceToString,
            ConfigurationInput configurationInput,
            @Value("classpath:user-x-vm.tf") Resource userXVM,
            @Value("classpath:user-x-firewall.tf") Resource userXFirewall
    ) throws IOException {
        this.configurationInput = configurationInput;
        this.userXVM = resourceToString.resourceToString(userXVM);
        this.userXFirewall = resourceToString.resourceToString(userXFirewall);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = args.getOptionValues("path").get(0);

        int index = 2;
        for (ConfigurationInput.User user : configurationInput.users()) {
            String currentUserXVM = userXVM
                    .replace("user-x-subnet-id", String.format("user_%s_subnet", index))
                    .replace("{user-x-subnet-name}", String.format("user-%s-subnet", index))
                    .replace("{user-x-subnet-range}", String.format("10.1.%s.0/24", index))
                    .replace("user-x-ip-id", String.format("user_%s_ip", index))
                    .replace("{user-x-ip-name}", String.format("user-%s-ip", index))
                    .replace("user-x-vm-id", String.format("user_%s_vm", index))
                    .replace("{user-x-vm-name}", String.format("user-%s-vm", index))
                    .replace("{user-machine-type}", configurationInput.userMachineType())
                    .replace("{user-x-owner}", String.format("user-%s", index))
                    .replace("user-x-vm-public-ip-output-id", String.format("user_%s_vm_public_ip_output", index))
                    .replace("user-x-vm-private-ip-output-id", String.format("user_%s_vm_private_ip_output", index));

            String currentUserXFirewall = userXFirewall
                    .replace("user-x-ingress-internal-id", String.format("user_%s_ingress_internal", index))
                    .replace("{user-x-ingress-internal-name}", String.format("user-%s-ingress-internal", index))
                    .replace("{user-x-ingress-destination-range}", String.format("10.1.%s.0/24", index))
                    .replace("user-x-ingress-external-id", String.format("user_%s_ingress_external", index))
                    .replace("{user-x-ingress-external-name}", String.format("user-%s-ingress-external", index));

            Files.writeString(Path.of(path + String.format("/user-%s-vm.tf", index)), currentUserXVM);
            Files.writeString(Path.of(path + String.format("/user-%s-firewall.tf", index)), currentUserXFirewall);

            index++;
        }
    }
}
