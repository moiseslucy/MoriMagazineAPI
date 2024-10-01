package com.api.MoriMagazineAPI.service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Component
public class SecretsManagerService {

    private final SecretsManagerClient secretsClient;

    public SecretsManagerService() {
        this.secretsClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1) // Substitua pela região correta
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    public String getSecret(String secretName) {
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    public JSONObject getParsedSecret(String secretName) {
        String secretString = getSecret(secretName);
        return new JSONObject(secretString);
    }
}
