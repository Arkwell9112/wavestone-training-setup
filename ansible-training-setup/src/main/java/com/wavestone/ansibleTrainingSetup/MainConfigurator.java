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
    private final InputConfiguration inputConfiguration;

    private final SSHHelper sshHelper;

    private final String provider;

    private final String vpc;

    private final String firewall;

    private final String serviceAccount;

    private final String awxVM;

    private final String awxFirewall;

    private final String ansibleCfg;

    private final String vars;

    private final String awxSetupPlaybook;

    private final String awxCheckPlaybook;

    private final String awxConfigurePlaybook;

    private final String publicInventoryGcp;

    private final String awxKusto;

    private final String awxPatch;

    private final String awxDeploy;

    public MainConfigurator(
            ResourceToString resourceToString,
            InputConfiguration inputConfiguration,
            SSHHelper sshHelper,
            @Value("classpath:terraform/provider.tf") Resource provider,
            @Value("classpath:terraform/vpc.tf") Resource vpc,
            @Value("classpath:terraform/firewall.tf") Resource firewall,
            @Value("classpath:terraform/service-account.tf") Resource serviceAccount,
            @Value("classpath:terraform/awx-vm.tf") Resource awxVM,
            @Value("classpath:terraform/awx-firewall.tf") Resource awxFirewall,
            @Value("classpath:ansible/ansible.cfg") Resource ansibleCfg,
            @Value("classpath:ansible/vars.yml") Resource vars,
            @Value("classpath:ansible/awx-setup-playbook.yml") Resource awxSetupPlaybook,
            @Value("classpath:ansible/awx-check-playbook.yml") Resource awxCheckPlaybook,
            @Value("classpath:ansible/awx-configure-playbook.yml") Resource awxConfigurePlaybook,
            @Value("classpath:ansible/public-inventory-gcp.yml") Resource publicInventoryGcp,
            @Value("classpath:k3s/kustomization.yml") Resource awxKusto,
            @Value("classpath:k3s/awx-deploy.yml") Resource awxDeploy,
            @Value("classpath:k3s/awx-patch.yml") Resource awxPatch
    ) throws IOException {
        this.inputConfiguration = inputConfiguration;
        this.sshHelper = sshHelper;
        this.provider = resourceToString.resourceToString(provider);
        this.vpc = resourceToString.resourceToString(vpc);
        this.firewall = resourceToString.resourceToString(firewall);
        this.serviceAccount = resourceToString.resourceToString(serviceAccount);
        this.awxVM = resourceToString.resourceToString(awxVM);
        this.awxFirewall = resourceToString.resourceToString(awxFirewall);
        this.ansibleCfg = resourceToString.resourceToString(ansibleCfg);
        this.vars = resourceToString.resourceToString(vars);
        this.awxSetupPlaybook = resourceToString.resourceToString(awxSetupPlaybook);
        this.awxCheckPlaybook = resourceToString.resourceToString(awxCheckPlaybook);
        this.awxConfigurePlaybook = resourceToString.resourceToString(awxConfigurePlaybook);
        this.publicInventoryGcp = resourceToString.resourceToString(publicInventoryGcp);
        this.awxKusto = resourceToString.resourceToString(awxKusto);
        this.awxPatch = resourceToString.resourceToString(awxPatch);
        this.awxDeploy = resourceToString.resourceToString(awxDeploy);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String path = args.getOptionValues("path").get(0);

        // Generate SSH key for awx-vm.
        sshHelper.generateSSHKey(path + "/ansible/awx-ssh-key");
        //Load the ssh key after generation.
        String sshKey = sshHelper.loadSSHPublicKeyConfigurationForTerraform(path + "/ansible/awx-ssh-key.pub");

        // Prepare terraform main config.
        String currentProvider = provider
                .replace("{project}", inputConfiguration.project())
                .replace("{region}", inputConfiguration.region())
                .replace("{zone}", inputConfiguration.zone());
        String currentAwxVM = awxVM
                .replace("{awx-machine-type}", inputConfiguration.awxMachineType())
                .replace("{ssh-key}", sshKey);
        String currentServiceAccount = serviceAccount
                .replace("{project}", inputConfiguration.project());

        // Write terraform main config.
        Files.writeString(Path.of(path + "/terraform/provider.tf"), currentProvider);
        Files.writeString(Path.of(path + "/terraform/vpc.tf"), vpc);
        Files.writeString(Path.of(path + "/terraform/firewall.tf"), firewall);
        Files.writeString(Path.of(path + "/terraform/service-account.tf"), currentServiceAccount);
        Files.writeString(Path.of(path + "/terraform/awx-vm.tf"), currentAwxVM);
        Files.writeString(Path.of(path + "/terraform/awx-firewall.tf"), awxFirewall);

        // Prepare ansible main config.
        String currentVars = vars
                .replace("{awx-operator-version}", inputConfiguration.awxOperatorVersion());
        String currentPublicInventoryGcp = publicInventoryGcp
                .replace("{project}", inputConfiguration.project())
                .replace("{zone}", inputConfiguration.zone());

        // Write ansible main config.
        Files.writeString(Path.of(path + "/ansible/ansible.cfg"), ansibleCfg);
        Files.writeString(Path.of(path + "/ansible/vars.yml"), currentVars);
        Files.writeString(Path.of(path + "/ansible/awx-setup-playbook.yml"), awxSetupPlaybook);
        Files.writeString(Path.of(path + "/ansible/awx-check-playbook.yml"), awxCheckPlaybook);
        Files.writeString(Path.of(path + "/ansible/awx-configure-playbook.yml"), awxConfigurePlaybook);
        Files.writeString(Path.of(path + "/ansible/public-inventory-gcp.yml"), currentPublicInventoryGcp);

        // Prepare k3s main config.
        String currentAWXKusto = awxKusto
                .replace("{awx-operator-version}", inputConfiguration.awxOperatorVersion());
        String currentAWXPatch = awxPatch
                .replace("{awx-operator-version}", inputConfiguration.awxOperatorVersion());

        // Write k3s main config.
        Files.writeString(Path.of(path + "/k3s/kustomization.yml"), currentAWXKusto);
        Files.writeString(Path.of(path + "/k3s/awx-patch.yml"), currentAWXPatch);
        Files.writeString(Path.of(path + "/k3s/awx-deploy.yml"), awxDeploy);
    }
}
