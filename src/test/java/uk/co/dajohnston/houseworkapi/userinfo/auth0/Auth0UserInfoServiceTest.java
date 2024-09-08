package uk.co.dajohnston.houseworkapi.userinfo.auth0;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Answers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
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

  @Mock private RequestBodyUriSpec requestBodyUriSpec;

  @SuppressWarnings("rawtypes")
  @Mock
  private RequestHeadersSpec requestHeadersSpec;

  @Mock private RequestBodySpec requestBodySpec;

  @Mock private ResponseSpec responseSpec;
  private Auth0UserInfoService auth0UserInfoService;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    autoCloseable = openMocks(this);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(webClient.patch()).thenReturn(requestBodyUriSpec);
    when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodyUriSpec.uri(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.attributes(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.accept(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    auth0UserInfoService =
        new Auth0UserInfoService(webClient, getMapper(Auth0UserInfoMapper.class));
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }

  @Test
  void getUserInfo_setsFirstNameFromMetaData() {
    Auth0User auth0User =
        new Auth0User("Joe", "", "", "", "", new Auth0UserMetaData("David", "", "", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.firstName(), is("David"));
  }

  @Test
  void getUserInfo_setsFirstNameFromGivenNameWhenNotSetInMetaData() {
    Auth0User auth0User =
        new Auth0User("Joe", "", "", "", "", new Auth0UserMetaData(null, "", "", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.firstName(), is("Joe"));
  }

  @Test
  void getUserInfo_setsLastNameFromMetaData() {
    Auth0User auth0User =
        new Auth0User("", "Bloggs", "", "", "", new Auth0UserMetaData("", "Johnston", "", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.lastName(), is("Johnston"));
  }

  @Test
  void getUserInfo_setsLastNameFromFamilyNameWhenNotSetInMetaData() {
    Auth0User auth0User =
        new Auth0User("", "Bloggs", "", "", "", new Auth0UserMetaData("", null, "", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.lastName(), is("Bloggs"));
  }

  @Test
  void getUserInfo_setsNicknameFromMetaData() {
    Auth0User auth0User =
        new Auth0User("", "", "", "Dave", "", new Auth0UserMetaData("", "", "DJ", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.nickname(), is("DJ"));
  }

  @Test
  void getUserInfo_setsNicknameFromTopLevel_whenNotPresentInMetaData() {
    Auth0User auth0User =
        new Auth0User("", "", "", "Dave", "", new Auth0UserMetaData("", "", null, ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.nickname(), is("Dave"));
  }

  @Test
  void getUserInfo_setsEmailFromTopLevel() {
    Auth0User auth0User =
        new Auth0User("", "", "david@test.com", "", "", new Auth0UserMetaData("", "", "", ""));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.email(), is("david@test.com"));
  }

  @Test
  void getUserInfo_setsPictureFromMetaData() {
    Auth0User auth0User =
        new Auth0User(
            "",
            "",
            "",
            "",
            "https://picture.com",
            new Auth0UserMetaData("", "", "", "https://picture-meta.com"));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.picture(), is("https://picture-meta.com"));
  }

  @Test
  void getUserInfo_setsPictureFromTopLevel_whenNotPresentInMetaData() {
    Auth0User auth0User =
        new Auth0User(
            "", "", "", "", "https://picture.com", new Auth0UserMetaData("", "", "", null));
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(just(auth0User));

    UserInfoDTO userInfo = auth0UserInfoService.getUserInfo("");

    assertThat(userInfo.picture(), is("https://picture.com"));
  }

  @Test
  void getUserInfo_sendsRequestToUsersAPIWithUserId() {
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(empty());

    auth0UserInfoService.getUserInfo("ABC123");

    verify(requestHeadersUriSpec).uri("users/{id}", "ABC123");
  }

  @Test
  void updateUserInfo_setsFirstNameInMetaData() {
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(empty());

    auth0UserInfoService.updateUserInfo("", new UserInfoDTO(null, "David", null, null, null));

    verify(requestBodySpec)
        .bodyValue(
            new Auth0User(
                null, null, null, null, null, new Auth0UserMetaData("David", null, null, null)));
  }

  @Test
  void updateUserInfo_setsLastNameInMetaData() {
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(empty());

    auth0UserInfoService.updateUserInfo("", new UserInfoDTO(null, null, "Johnston", null, null));

    verify(requestBodySpec)
        .bodyValue(
            new Auth0User(
                null, null, null, null, null, new Auth0UserMetaData(null, "Johnston", null, null)));
  }

  @Test
  void updateUserInfo_setsNicknameInMetaData() {
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(empty());

    auth0UserInfoService.updateUserInfo("", new UserInfoDTO(null, null, null, "DJ", null));

    verify(requestBodySpec)
        .bodyValue(
            new Auth0User(
                null, null, null, null, null, new Auth0UserMetaData(null, null, "DJ", null)));
  }

  @Test
  void updateUserInfo_setsPictureInMetaData() {
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(empty());

    auth0UserInfoService.updateUserInfo(
        "", new UserInfoDTO(null, null, null, null, "https://picture.com/dj"));

    verify(requestBodySpec)
        .bodyValue(
            new Auth0User(
                null,
                null,
                null,
                null,
                null,
                new Auth0UserMetaData(null, null, null, "https://picture.com/dj")));
  }

  @Test
  void updateUserInfo_sendsRequestToUsersAPIWithUserId() {
    when(responseSpec.bodyToMono(Auth0User.class)).thenReturn(empty());

    auth0UserInfoService.updateUserInfo("ABC123", new UserInfoDTO(null, null, null, null, null));

    verify(requestBodyUriSpec).uri("users/{id}", "ABC123");
  }

  @Test
  void updateUserInfo_returnsUpdatedUserInfo() {
    when(responseSpec.bodyToMono(Auth0User.class))
        .thenReturn(
            just(
                new Auth0User(
                    "Joe",
                    "Bloggs",
                    "dj@test.com",
                    "JB",
                    "https://picture.com/jb",
                    new Auth0UserMetaData("David", "Johnston", "DJ", "https://picture.com/dj"))));

    UserInfoDTO userInfoDTO =
        auth0UserInfoService.updateUserInfo(
            "ABC123", new UserInfoDTO(null, "David", "Johnston", "DJ", "https://picture.com/dj"));

    assertThat(
        userInfoDTO,
        is(new UserInfoDTO("dj@test.com", "David", "Johnston", "DJ", "https://picture.com/dj")));
  }
}
