package com.wavestone.ansibleTrainingSetup.awxAPI;

public record CredentialCreationRequest(
        String name,
        String description,
        String organization,
        String credential_type,
        CredentialInput inputs
) {
    public record CredentialInput(
            String username,
            String ssh_key_data,
            String become_method,
            String become_username
    ) {
    }
}
