package com.wavestone.ansibleTrainingSetup.awxAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavestone.ansibleTrainingSetup.ResourceToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HTTPHelper {
    private static final Logger logger = LoggerFactory.getLogger(HTTPHelper.class);

    private final ApplicationArguments applicationArguments;

    private final ObjectMapper objectMapper;

    private final ResourceToString resourceToString;

    public HTTPHelper(ApplicationArguments applicationArguments, ObjectMapper objectMapper, ResourceToString resourceToString) {
        this.applicationArguments = applicationArguments;
        this.objectMapper = objectMapper;
        this.resourceToString = resourceToString;
    }

    public <T> T request(
            String method,
            String path,
            Object body,
            Class<T> response
    ) throws IOException {
        String ip = applicationArguments.getOptionValues("awxIP").get(0);
        String port = applicationArguments.getOptionValues("awxPort").get(0);
        String username = applicationArguments.getOptionValues("awxUsername").get(0);
        String password = applicationArguments.getOptionValues("awxPassword").get(0);

        URL url = new URL("http://" + ip + ":" + port + "/api/v2" + path);

        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream errorStream = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)));

            if (body != null) {
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();
                objectMapper.writeValue(outputStream, body);
            }

            int code = connection.getResponseCode();
            if (code < 200 || code >= 300) {
                errorStream = connection.getErrorStream();
                logger.error("Failed request:\n{}", resourceToString.streamToString(errorStream));
                throw new IOException("Cannot make request.")
            }

            inputStream = connection.getInputStream();

            return objectMapper.readValue(inputStream, response);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (errorStream != null) {
                errorStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
