package com.wavestone.ansibleTrainingSetup.awxAPI;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AWXHelper {
    private final HTTPHelper httpHelper;

    private final String path;

    public AWXHelper(HTTPHelper httpHelper, ApplicationArguments applicationArguments) {
        this.httpHelper = httpHelper;
        this.path = applicationArguments.getOptionValues("path").get(0);
    }

    public ElementList.ElementResponse createOrganization() throws Exception {
        try {
            OrganizationCreationRequest organizationCreationRequest = new OrganizationCreationRequest(
                    "Training",
                    "Organization for training at Wavestone."
            );

            return httpHelper.request(
                    "POST",
                    "/organizations/",
                    organizationCreationRequest,
                    ElementList.ElementResponse.class
            );
        } catch (Exception e) {
            ElementList organizations = httpHelper.request(
                    "GET",
                    "/organizations/",
                    null,
                    ElementList.class
            );

            for (ElementList.ElementResponse organization : organizations.results()) {
                if (organization.name().equals("Training")) return organization;
            }

            throw new Exception("Cannot make validation request.");
        }
    }

    public ElementList.ElementResponse createCredential(String organization) throws Exception {
        String sshKey = Files.readString(Path.of(path + "/ansible/ssh-key"));

        try {
            CredentialCreationRequest credentialCreationRequest = new CredentialCreationRequest(
                    "MAIN",
                    "MAIN credential for training infrastructure.",
                    organization,
                    "1",
                    new CredentialCreationRequest.CredentialInput(
                            "ansible",
                            sshKey,
                            "sudo",
                            ""
                    )
            );

            return httpHelper.request(
                    "POST",
                    "/credentials/",
                    credentialCreationRequest,
                    ElementList.ElementResponse.class
            );
        } catch (Exception e) {
            ElementList credentials = httpHelper.request(
                    "GET",
                    "/credentials/",
                    null,
                    ElementList.class
            );

            for (ElementList.ElementResponse credential : credentials.results()) {
                if (credential.name().equals("MAIN")) return credential;
            }

            throw new Exception("Cannot make validation request.");
        }
    }
}
