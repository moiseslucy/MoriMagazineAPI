package com.api.MoriMagazineAPI.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import org.json.JSONObject;

@Service
public class EnvironmentVariableService {

    public void loadSecretsIntoEnv() {
        // Nome do segredo no AWS Secrets Manager
        String secretName = "local/MoriMagazine/MySQL";
        String region = "us-east-1";  // Altere para sua região

        // Criar o cliente do AWS Secrets Manager
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .build();

        // Criar a solicitação para buscar o segredo
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        // Buscar o segredo
        GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);

        // Obter o valor do segredo em formato de string
        String secret = getSecretValueResponse.secretString();

        // Transformar o segredo em JSON para acessar as chaves e valores
        JSONObject secretJson = new JSONObject(secret);

        // Definir as variáveis de ambiente no sistema
        System.setProperty("DB_URL", secretJson.getString("spring.datasource.url"));
        System.setProperty("DB_USERNAME", secretJson.getString("spring.datasource.username"));
        System.setProperty("DB_PASSWORD", secretJson.getString("spring.datasource.password"));
        System.setProperty("MAIL_HOST", secretJson.getString("spring.mail.host"));
        System.setProperty("MAIL_PORT", secretJson.getString("spring.mail.port"));
        System.setProperty("MAIL_USERNAME", secretJson.getString("spring.mail.username"));
        System.setProperty("MAIL_PASSWORD", secretJson.getString("spring.mail.password"));
    }
}
