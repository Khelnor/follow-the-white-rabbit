package com.khelnor.lab.oauth;

import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;

@Service
public class TokenProvider {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public TokenProvider(ClientRegistrationRepository registrations, OAuth2AuthorizedClientService clientService) {

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clientService);

        manager.setAuthorizedClientProvider(provider);

        this.authorizedClientManager = manager;
    }

    public String getJwtToken() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId("rabbitmq-client")
                .principal("follow-the-white-rabbit")
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(request);

        if (client == null) {
            throw new IllegalStateException("Unable to get a JWT token !");
        }

        return client.getAccessToken().getTokenValue();
    }
}
