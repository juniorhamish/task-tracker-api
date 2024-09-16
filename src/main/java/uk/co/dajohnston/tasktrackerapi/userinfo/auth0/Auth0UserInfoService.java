package uk.co.dajohnston.tasktrackerapi.userinfo.auth0;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static reactor.core.publisher.Mono.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfo;
import uk.co.dajohnston.tasktrackerapi.userinfo.service.UserInfoService;

@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0UserInfoService implements UserInfoService {

  private final WebClient webClient;
  private final Auth0UserInfoMapper auth0UserInfoMapper;

  public UserInfo getUserInfo(String id) {
    return auth0UserInfoMapper.toUserInfo(
        webClient
            .get()
            .uri("users/{id}", id)
            .attributes(clientRegistrationId("auth0"))
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Auth0User.class)
            .block());
  }

  @Override
  public UserInfo updateUserInfo(String id, UserInfo userInfo) {
    return auth0UserInfoMapper.toUserInfo(
        webClient
            .patch()
            .uri("users/{id}", id)
            .attributes(clientRegistrationId("auth0"))
            .bodyValue(auth0UserInfoMapper.toAuth0User(userInfo))
            .headers(httpHeaders -> httpHeaders.setContentType(APPLICATION_JSON))
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                (ClientResponse clientResponse) ->
                    clientResponse
                        .bodyToMono(Auth0ErrorResponse.class)
                        .flatMap(
                            (Auth0ErrorResponse auth0ErrorResponse) -> {
                              logResponse(clientResponse, auth0ErrorResponse);
                              return error(
                                  new ResponseStatusException(
                                      clientResponse.statusCode(), auth0ErrorResponse.message()));
                            }))
            .bodyToMono(Auth0User.class)
            .block());
  }

  private void logResponse(ClientResponse response, Auth0ErrorResponse auth0ErrorResponse) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
    log.error("Failed to invoke user management API: {}", auth0ErrorResponse.message());
  }

  private record Auth0ErrorResponse(String message) {}
}
