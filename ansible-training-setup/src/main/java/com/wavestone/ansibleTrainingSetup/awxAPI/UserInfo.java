package com.wavestone.ansibleTrainingSetup.awxAPI;

import com.wavestone.ansibleTrainingSetup.InputConfiguration;

public record UserInfo(
        int id,
        String username,
        InputConfiguration.User.Role role,
        String password,
        String firstVMPrivateIP,
        String firstVMPublicIP,
        String secondVMPrivateIP,
        String secondVMPublicIP
) {
}
