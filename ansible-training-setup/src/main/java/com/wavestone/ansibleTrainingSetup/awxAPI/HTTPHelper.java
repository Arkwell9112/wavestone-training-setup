package com.wavestone.ansibleTrainingSetup.awxAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class HTTPHelper {
    private static final Logger logger = LoggerFactory.getLogger(HTTPHelper.class);

    private final ApplicationArguments applicationArguments;

    private final ObjectMapper objectMapper;

    public HTTPHelper(ApplicationArguments applicationArguments, ObjectMapper objectMapper) {
        this.applicationArguments = applicationArguments;
        this.objectMapper = objectMapper;
    }

    public <T> T request(
            String method,
            String path,
            Object body,
            Class<T> response
    ) throws Exception {
        String ip = applicationArguments.getOptionValues("awxIP").get(0);
        String port = applicationArguments.getOptionValues("awxPort").get(0);
        String username = applicationArguments.getOptionValues("awxUsername").get(0);
        String password = applicationArguments.getOptionValues("awxPassword").get(0);

        HttpRequest.BodyPublisher bodyPublisher = null;
        if (body != null) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body));
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://" + ip + ":" + port + "/api/v2" + path))
                .method(method, bodyPublisher)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)))
                .build();

        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() < 200 || httpResponse.statusCode() > 299) {
            logger.error("Failed request :\n {}", httpResponse.body());
            throw new Exception("Cannot do request.");
        }

        if (response != null) {
            return objectMapper.readValue(httpResponse.body(), response);
        }

        return null;
    }
}
