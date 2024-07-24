package com.wavestone.ansibleTrainingSetup;

import java.util.List;

public record ConfigurationInput(
        String project,
        String region,
        String zone,
        String awxMachineType,
        String userMachineType,
        String awxVersion,
        List<User> users
) {
    public record User(
            String username,
            Role role
    ) {
        public enum Role {
            TRAINER, TRAINEE;
        }
    }
}
