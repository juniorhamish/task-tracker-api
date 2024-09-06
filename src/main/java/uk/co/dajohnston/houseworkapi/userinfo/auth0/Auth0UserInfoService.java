package uk.co.dajohnston.houseworkapi.userinfo.auth0;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;
import uk.co.dajohnston.houseworkapi.userinfo.service.UserInfoService;

@Service
@RequiredArgsConstructor
public class Auth0UserInfoService implements UserInfoService {

  private final WebClient webClient;
  private final Auth0UserInfoMapper auth0UserInfoMapper;

  public UserInfoDTO getUserInfo(String id) {
    return auth0UserInfoMapper.toUserInfoDTO(
        webClient
            .get()
            .uri("users/" + id)
            .attributes(clientRegistrationId("auth0"))
            .accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Auth0User.class)
            .block());
  }
}
