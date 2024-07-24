package com.wavestone.ansibleTrainingSetup;

import java.util.List;

public record InputConfiguration(
        String project,
        String region,
        String zone,
        String awxMachineType,
        String userMachineType,
        String awxOperatorVersion,
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
