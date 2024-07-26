package com.wavestone.ansibleTrainingSetup.awxAPI;

public record UserCreationRequest(
        String username,
        boolean is_superuser,
        String password
) {
}
