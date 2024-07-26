package com.wavestone.ansibleTrainingSetup.awxAPI;

import com.wavestone.ansibleTrainingSetup.ResourceToString;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TerraformHelper {
    private final ResourceToString resourceToString;

    public TerraformHelper(ResourceToString resourceToString) {
        this.resourceToString = resourceToString;
    }

    public String getUserVMPrivateIPFirst(int index) throws InterruptedException, IOException {
        return processWithResult(String.format("terraform -chdir=terraform output -raw user_%s_vm_private_ip_first", index));
    }

    public String getUserVMPublicIPFirst(int index) throws InterruptedException, IOException {
        return processWithResult(String.format("terraform -chdir=terraform output -raw user_%s_vm_public_ip_first", index));
    }

    public String getUserVMPrivateIPSecond(int index) throws InterruptedException, IOException {
        return processWithResult(String.format("terraform -chdir=terraform output -raw user_%s_vm_private_ip_second", index));
    }

    public String getUserVMPublicIPSecond(int index) throws InterruptedException, IOException {
        return processWithResult(String.format("terraform -chdir=terraform output -raw user_%s_vm_public_ip_second", index));
    }

    private String processWithResult(String command) throws IOException, InterruptedException {
        InputStream inputStream = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            inputStream = process.getInputStream();
            return resourceToString.streamToString(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
