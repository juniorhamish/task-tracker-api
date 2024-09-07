package uk.co.dajohnston.houseworkapi.userinfo.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.houseworkapi.security.SecurityConfig;
import uk.co.dajohnston.houseworkapi.security.WithMockJWT;
import uk.co.dajohnston.houseworkapi.userinfo.model.UserInfoDTO;
import uk.co.dajohnston.houseworkapi.userinfo.service.UserInfoService;

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
            new UserInfoDTO(
                "email@test.com",
                "David",
                "Johnston",
                "DJ",
                "https://picture.com"));

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
                          "picture": "https://picture.com"
                        }
                        """));
  }

  @Test
  @WithMockJWT(subject = "abc123")
  void get_retrievesUserInfoUsingTheLoggedInUser() throws Exception {
    mockMvc.perform(get("/userinfo")).andExpect(status().isOk());

    verify(userInfoService).getUserInfo("abc123");
  }
}
