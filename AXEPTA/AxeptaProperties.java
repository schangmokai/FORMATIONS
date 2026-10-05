package com.example.axepta;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "axepta")
public record AxeptaProperties(
        String tokenUri,
        String baseUrl,
        String clientId,
        String clientSecret,
        AuthMethod clientAuthMethod,
        String scope,
        long expiryMarginSeconds
) {
    public enum AuthMethod { BASIC, POST }

    public AxeptaProperties {
        if (clientAuthMethod == null) clientAuthMethod = AuthMethod.BASIC;
        if (expiryMarginSeconds <= 0) expiryMarginSeconds = 60;
    }
}