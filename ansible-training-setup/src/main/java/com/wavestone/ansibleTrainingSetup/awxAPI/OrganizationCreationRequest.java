package com.wavestone.ansibleTrainingSetup.awxAPI;

public record OrganizationCreationRequest(
        String name,
        String description
) {
}
