package uk.co.dajohnston.houseworkapi.userinfo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.houseworkapi.security.WithMockJWT;

@SpringBootTest
@AutoConfigureMockMvc
class UserInfoTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockJWT(subject = "auth0|66dad01cbf661955d9db2534")
  void getUserInfo_retrievesDataFromAuth0() throws Exception {
    mockMvc
        .perform(get("/userinfo").with(csrf()))
        .andExpect(
            content()
                .json(
                    """
                        {
                          "email": "housework-api-integration-tests@dajohnston.co.uk",
                          "firstName": null,
                          "lastName": null,
                          "nickname": "housework-api-integration-tests",
                          "picture": "https://s.gravatar.com/avatar/7b91293197ba56e160fa3411c320b70e?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Fho.png"
                        }
                        """));
  }
}
