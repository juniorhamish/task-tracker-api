package uk.co.dajohnston.houseworkapi.userinfo.auth0;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Answers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;

class Auth0UserInfoServiceTest {

  private AutoCloseable autoCloseable;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private WebClient webClient;

  @SuppressWarnings("rawtypes")
  @Mock
  private RequestHeadersUriSpec requestHeadersUriSpec;

  @SuppressWarnings("rawtypes")
  @Mock
  private RequestHeadersSpec requestHeadersSpec;

  @Mock private ResponseSpec responseSpec;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    autoCloseable = openMocks(this);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.attributes(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  void getUserInfo_setsFirstNameFromMetaData() {
    Auth0User auth0User = new Auth0User("", "", "", "", new Auth0UserMetaData("David", "", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.firstName(), is("David"));
  }

  @Test
  void getUserInfo_setsLastNameFromMetaData() {
    Auth0User auth0User = new Auth0User("", "", "", "", new Auth0UserMetaData("", "Johnston", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.lastName(), is("Johnston"));
  }

  @Test
  void getUserInfo_setsNicknameFromMetaData_whenPresent() {
    Auth0User auth0User = new Auth0User("", "", "Dave", "", new Auth0UserMetaData("", "", "DJ"));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.nickname(), is("DJ"));
  }

  @Test
  void getUserInfo_setsNicknameFromTopLevel_whenNotPresentInMetaData() {
    Auth0User auth0User = new Auth0User("", "", "Dave", "", new Auth0UserMetaData("", "", null));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.nickname(), is("Dave"));
  }

  @Test
  void getUserInfo_setsNameFromTopLevel() {
    Auth0User auth0User = new Auth0User("", "David Johnston", "", "", null);
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.name(), is("David Johnston"));
  }

  @Test
  void getUserInfo_setsEmailFromTopLevel() {
    Auth0User auth0User = new Auth0User("david@test.com", "", "", "", null);
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.email(), is("david@test.com"));
  }

  @Test
  void getUserInfo_setsPictureFromTopLevel() {
    Auth0User auth0User = new Auth0User("", "", "", "https://picture.com", null);
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class)).getUserInfo("");

    assertThat(userInfo.picture(), is("https://picture.com"));
  }
}