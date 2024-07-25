package com.wavestone.ansibleTrainingSetup;

import com.wavestone.ansibleTrainingSetup.awxAPI.AWXHelper;
import com.wavestone.ansibleTrainingSetup.awxAPI.ElementList;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("configure")
@Order(0)
public class AWXConfigurator implements ApplicationRunner {
    private final AWXHelper awxHelper;

    public AWXConfigurator(AWXHelper awxHelper) {
        this.awxHelper = awxHelper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ElementList.ElementResponse organization = awxHelper.createOrganization();
        ElementList.ElementResponse credential = awxHelper.createCredential(organization.id());
    }
}
