package com.example.axepta;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AxeptaPaymentService {

    private final RestClient axeptaRestClient;

    public AxeptaPaymentService(RestClient axeptaRestClient) {
        this.axeptaRestClient = axeptaRestClient;
    }

    public PaymentResponse createPayment(PaymentRequest request) {
        return axeptaRestClient.post()
                .uri("/payments")            // adaptez au chemin réel de l'API Axepta
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }
}