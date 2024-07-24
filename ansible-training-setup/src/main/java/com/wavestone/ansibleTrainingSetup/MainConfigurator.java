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
@Order(0)
public class MainConfigurator implements ApplicationRunner {
    private final ConfigurationInput configurationInput;

    private final SSHKeyConfiguration sshKeyConfiguration;

    private final String provider;

    private final String vpc;

    private final String firewall;

    private final String serviceAccount;

    private final String awxVM;

    private final String awxFirewall;

    public MainConfigurator(
            ResourceToString resourceToString,
            ConfigurationInput configurationInput,
            SSHKeyConfiguration sshKeyConfiguration,
            @Value("classpath:provider.tf") Resource provider,
            @Value("classpath:vpc.tf") Resource vpc,
            @Value("classpath:firewall.tf") Resource firewall,
            @Value("classpath:service-account.tf") Resource serviceAccount,
            @Value("classpath:awx-vm.tf") Resource awxVM,
            @Value("classpath:awx-firewall.tf") Resource awxFirewall
    ) throws IOException {
        this.configurationInput = configurationInput;
        this.sshKeyConfiguration = sshKeyConfiguration;
        this.provider = resourceToString.resourceToString(provider);
        this.vpc = resourceToString.resourceToString(vpc);
        this.firewall = resourceToString.resourceToString(firewall);
        this.serviceAccount = resourceToString.resourceToString(serviceAccount);
        this.awxVM = resourceToString.resourceToString(awxVM);
        this.awxFirewall = resourceToString.resourceToString(awxFirewall);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String currentProvider = provider
                .replace("{project}", configurationInput.project())
                .replace("{region}", configurationInput.region())
                .replace("{zone}", configurationInput.zone());
        String currentAwxVM = awxVM
                .replace("{awx-machine-type}", configurationInput.awxMachineType())
                .replace("{ssh-key}", sshKeyConfiguration.sshKey());

        String path = args.getOptionValues("path").get(0);

        Files.writeString(Path.of(path + "/provider.tf"), currentProvider);
        Files.writeString(Path.of(path + "/vpc.tf"), vpc);
        Files.writeString(Path.of(path + "/firewall.tf"), firewall);
        Files.writeString(Path.of(path + "/service-account.tf"), serviceAccount);
        Files.writeString(Path.of(path + "/awx-vm.tf"), currentAwxVM);
        Files.writeString(Path.of(path + "/awx-firewall.tf"), awxFirewall);

        String ansiblePath = args.getOptionValues("ansiblePath").get(0);

        Path inventoryPath = Path.of(ansiblePath + "/public-inventory-gcp.yml");

        String publicGCPInventory = Files.readString(inventoryPath)
                .replace("{project}", configurationInput.project())
                .replace("{zone}", configurationInput.zone());

        Files.writeString(inventoryPath, publicGCPInventory);

        Path varsPath = Path.of(ansiblePath + "/vars.yml");

        String vars = Files.readString(varsPath)
                .replace("{awx-operator-version}", configurationInput.awxOperatorVersion());

        Files.writeString(varsPath, vars);

        Path kustomizationPath = Path.of(ansiblePath + "/kustomization.yml");

        String kustomization = Files.readString(varsPath)
                .replace("{awx-operator-version}", configurationInput.awxOperatorVersion());

        Files.writeString(kustomizationPath, kustomization);
    }
}
