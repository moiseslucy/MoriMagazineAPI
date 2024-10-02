package com.api.MoriMagazineAPI.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;
import software.amazon.awssdk.services.ses.model.SesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SesService {

    private static final Logger logger = LoggerFactory.getLogger(SesService.class);

    private final SesClient sesClient;

    @Autowired
    public SesService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public void verifyEmail(String emailAddress) {
        try {
            VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
                    .emailAddress(emailAddress)
                    .build();

            sesClient.verifyEmailIdentity(request);
            logger.info("Solicitação de verificação de e-mail enviada para: {}", emailAddress);
        } catch (SesException e) {
            logger.error("Erro ao verificar email no SES: {}", e.awsErrorDetails().errorMessage());
        }
    }
}
