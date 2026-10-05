package com.example.axepta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class AxeptaTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(AxeptaTokenProvider.class);

    private final AxeptaProperties props;
    private final RestClient authClient;

    private volatile String accessToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public AxeptaTokenProvider(AxeptaProperties props, RestClient.Builder builder) {
        this.props = props;
        this.authClient = builder.build(); // client SANS intercepteur, pour éviter une boucle
    }

    /** Renvoie le token en cache, ou en demande un nouveau s'il est expiré ou proche de l'expiration. */
    public String getToken() {
        if (isValid()) {
            return accessToken;
        }
        synchronized (this) {
            if (!isValid()) {      // double vérification : un seul thread rafraîchit
                refresh();
            }
            return accessToken;
        }
    }

    /** Force le renouvellement au prochain appel (par exemple après un 401). */
    public synchronized void invalidate() {
        this.expiresAt = Instant.EPOCH;
    }

    private boolean isValid() {
        return accessToken != null
                && Instant.now().isBefore(expiresAt.minusSeconds(props.expiryMarginSeconds()));
    }

    private void refresh() {
        log.info("Demande d'un nouveau token Axepta");

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        if (StringUtils.hasText(props.scope())) {
            form.add("scope", props.scope());
        }
        if (props.clientAuthMethod() == AxeptaProperties.AuthMethod.POST) {
            form.add("client_id", props.clientId());
            form.add("client_secret", props.clientSecret());
        }

        TokenResponse response = authClient.post()
                .uri(props.tokenUri())
                .headers(h -> {
                    if (props.clientAuthMethod() == AxeptaProperties.AuthMethod.BASIC) {
                        h.setBasicAuth(props.clientId(), props.clientSecret());
                    }
                })
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (response == null || !StringUtils.hasText(response.accessToken())) {
            throw new IllegalStateException("Réponse token Axepta invalide");
        }

        this.accessToken = response.accessToken();
        long ttl = response.expiresIn() > 0 ? response.expiresIn() : 300; // valeur de secours
        this.expiresAt = Instant.now().plusSeconds(ttl);

        log.info("Token Axepta obtenu, valide {} s", ttl); // ne jamais logger le token lui-même
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") long expiresIn) {
    }
}