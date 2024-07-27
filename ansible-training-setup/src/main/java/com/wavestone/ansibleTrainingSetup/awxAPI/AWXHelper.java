package com.wavestone.ansibleTrainingSetup.awxAPI;

import org.springframework.stereotype.Component;

@Component
public class AWXHelper {
    private final HTTPHelper httpHelper;

    public AWXHelper(HTTPHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    public ElementList.ElementResponse createOrganization(String name, String description) throws Exception {
        try {
            OrganizationCreationRequest organizationCreationRequest = new OrganizationCreationRequest(
                    name,
                    description
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
                if (organization.name().equals(name)) return organization;
            }

            throw new Exception("Cannot make validation request.");
        }
    }

    public ElementList.ElementResponse createUserInOrganization(String organization, String username, boolean isSuperUser, String password, boolean forcePassword) throws Exception {
        try {
            UserCreationRequest userCreationRequest = new UserCreationRequest(
                    username,
                    isSuperUser,
                    password
            );

            return httpHelper.request(
                    "POST",
                    "/organizations/" + organization + "/users/",
                    userCreationRequest,
                    ElementList.ElementResponse.class
            );
        } catch (Exception e) {
            ElementList users = httpHelper.request(
                    "GET",
                    "/organizations/" + organization + "/users/",
                    null,
                    ElementList.class
            );

            if (!forcePassword) {
                for (ElementList.ElementResponse user : users.results()) {
                    if (!user.username().equals(username)) return user;
                }
            } else {
                for (ElementList.ElementResponse user : users.results()) {
                    if (!user.username().equals(username)) continue;
                    httpHelper.request(
                            "PATCH",
                            "/users/" + user.id(),
                            new UserUpdateRequest(
                                    password
                            ),
                            null
                    );
                    return user;
                }
            }

            throw new Exception("Cannot make validation request.");
        }
    }

    public ElementList.ElementResponse associateRoleWithUser(String userId, String roleId) throws Exception {
        RoleAssociationRequest roleAssociationRequest = new RoleAssociationRequest(
                roleId
        );

        return httpHelper.request(
                "POST",
                "/users/" + userId + "/roles/",
                roleAssociationRequest,
                null
        );
    }

    public ElementList.ElementResponse createSSHMachineCredential(String name, String description, String organization, String privateKey) throws Exception {
        try {
            CredentialCreationRequest credentialCreationRequest = new CredentialCreationRequest(
                    name,
                    description,
                    organization,
                    "1",
                    new CredentialCreationRequest.CredentialInput(
                            "ansible",
                            privateKey,
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
                if (!credential.name().equals(name)) return credential;
            }

            throw new Exception("Cannot make validation request.");
        }
    }
}
