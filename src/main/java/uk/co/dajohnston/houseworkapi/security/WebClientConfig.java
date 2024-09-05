package uk.co.dajohnston.houseworkapi.security;

import static java.util.Collections.*;
import static org.springframework.util.CollectionUtils.toMultiValueMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
    var oauth2Client =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

    return WebClient.builder()
        .apply(oauth2Client.oauth2Configuration())
        .baseUrl("https://dajohnston.eu.auth0.com/api/v2/")
        .build();
  }

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientService authorizedClientService) {

    OAuth2AuthorizedClientProvider authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials(
                builder ->
                    builder
                        .accessTokenResponseClient(clientCredentialsAccessTokenResponseClient())
                        .build())
            .build();

    var authorizedClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  private static OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest>
      clientCredentialsAccessTokenResponseClient() {
    var accessTokenResponseClient = new DefaultClientCredentialsTokenResponseClient();

    var requestEntityConverter = new OAuth2ClientCredentialsGrantRequestEntityConverter();
    requestEntityConverter.addParametersConverter(
        foo ->
            toMultiValueMap(
                singletonMap(
                    "audience", singletonList("https://dajohnston.eu.auth0.com/api/v2/"))));
    accessTokenResponseClient.setRequestEntityConverter(requestEntityConverter);

    return accessTokenResponseClient;
  }
}
