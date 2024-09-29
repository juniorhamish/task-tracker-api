package uk.co.dajohnston.tasktrackerapi.userinfo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.dajohnston.tasktrackerapi.userinfo.AvatarSource.GRAVATAR;
import static uk.co.dajohnston.tasktrackerapi.userinfo.AvatarSource.MANUAL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.tasktrackerapi.security.SecurityConfig;
import uk.co.dajohnston.tasktrackerapi.security.WithMockJWT;
import uk.co.dajohnston.tasktrackerapi.userinfo.model.UserInfo;
import uk.co.dajohnston.tasktrackerapi.userinfo.service.UserInfoService;

@WebMvcTest(UserInfoController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockJWT
class UserInfoControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserInfoService userInfoService;

  @Test
  void get_returnsUserInfoFromService() throws Exception {
    when(userInfoService.getUserInfo(anyString()))
        .thenReturn(
            new UserInfo(
                "email@test.com",
                "David",
                "Johnston",
                "DJ",
                "https://picture.com",
                "gravatar@email.com",
                GRAVATAR));

    mockMvc
        .perform(get("/userinfo"))
        .andExpect(
            content()
                .json(
                    """
                        {
                          "firstName": "David",
                          "lastName": "Johnston",
                          "email": "email@test.com",
                          "nickname": "DJ",
                          "picture": "https://picture.com",
                          "gravatarEmailAddress": "gravatar@email.com",
                          "avatarImageSource": "GRAVATAR"
                        }
                        """));
  }

  @Test
  @WithMockJWT(subject = "abc123")
  void get_retrievesUserInfoUsingTheLoggedInUser() throws Exception {
    mockMvc.perform(get("/userinfo")).andExpect(status().isOk());

    verify(userInfoService).getUserInfo("abc123");
  }

  @Test
  void patch_rejectsPayloadContainingEmailAddress() throws Exception {
    mockMvc
        .perform(
            patch("/userinfo")
                .content(
                    """
                    {
                      "email": "email@test.com"
                    }
                    """)
                .with(csrf())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockJWT(subject = "MyUserId")
  void patch_usesServiceToUpdateUser() throws Exception {
    mockMvc
        .perform(
            patch("/userinfo")
                .content(
                    """
                    {
                      "firstName": "David",
                      "lastName": "Johnston",
                      "nickname": "DJ",
                      "picture": "https://picture.com/dj",
                      "gravatarEmailAddress": "gravatar@email.com",
                      "avatarImageSource": "GRAVATAR"
                    }
                    """)
                .with(csrf())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(userInfoService)
        .updateUserInfo(
            "MyUserId",
            new UserInfo(
                null,
                "David",
                "Johnston",
                "DJ",
                "https://picture.com/dj",
                "gravatar@email.com",
                GRAVATAR));
  }

  @Test
  void patch_returnsUpdatedUserInfo() throws Exception {
    when(userInfoService.updateUserInfo(anyString(), any()))
        .thenReturn(
            new UserInfo(
                "dj@test.com",
                "David",
                "Johnston",
                "DJ",
                "https://picture.com/dj",
                "gravatar@email.com",
                MANUAL));

    mockMvc
        .perform(
            patch("/userinfo")
                .content(
                    """
                    {
                      "firstName": "David",
                      "lastName": "Johnston",
                      "nickname": "DJ",
                      "picture": "https://picture.com/dj",
                      "gravatarEmailAddress": "gravatar@email.com",
                      "avatarImageSource": "MANUAL"
                    }
                    """)
                .with(csrf())
                .contentType(APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            content()
                .json(
                    """
                        {
                          "firstName": "David",
                          "lastName": "Johnston",
                          "email": "dj@test.com",
                          "nickname": "DJ",
                          "picture": "https://picture.com/dj"
                        }
                        """));
  }
}
