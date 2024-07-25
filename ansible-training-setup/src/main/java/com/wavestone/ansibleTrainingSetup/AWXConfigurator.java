package com.wavestone.ansibleTrainingSetup;

import com.wavestone.ansibleTrainingSetup.awxAPI.HTTPHelper;
import com.wavestone.ansibleTrainingSetup.awxAPI.ObjectResponse;
import com.wavestone.ansibleTrainingSetup.awxAPI.OrganizationCreationRequest;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("configure")
@Order(0)
public class AWXConfigurator implements ApplicationRunner {
    private final HTTPHelper httpHelper;

    public AWXConfigurator(HTTPHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        OrganizationCreationRequest organizationCreationRequest = new OrganizationCreationRequest(
                "Training",
                "Organization for training at Wavestone."
        );
        ObjectResponse organization = httpHelper.request(
                "POST",
                "/organizations",
                organizationCreationRequest,
                ObjectResponse.class
        );

        System.out.println(organization.id());
    }
}
