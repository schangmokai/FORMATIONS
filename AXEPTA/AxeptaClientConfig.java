package com.example.axepta;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(AxeptaProperties.class)
public class AxeptaClientConfig {

    @Bean
    public RestClient axeptaRestClient(RestClient.Builder builder,
                                       AxeptaProperties props,
                                       AxeptaTokenProvider tokenProvider) {
        return builder
                .baseUrl(props.baseUrl())
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(tokenProvider.getToken());
                    ClientHttpResponse response = execution.execute(request, body);

                    // Token révoqué ou expiré côté Axepta : on renouvelle et on réessaie UNE fois
                    if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        response.close();
                        tokenProvider.invalidate();
                        request.getHeaders().setBearerAuth(tokenProvider.getToken());
                        return execution.execute(request, body);
                    }
                    return response;
                })
                .build();
    }
}