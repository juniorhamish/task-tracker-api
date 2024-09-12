package uk.co.dajohnston.tasktrackerapi.userinfo;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.tasktrackerapi.security.WithMockJWT;

@SpringBootTest
@AutoConfigureMockMvc
class UserInfoTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @WithMockJWT(subject = "auth0|66dad01cbf661955d9db2534")
  void userInfo_patchUpdatesInfoRetrievedByGet() throws Exception {
    int random = ThreadLocalRandom.current().nextInt(100000, 1000000);

    mockMvc.perform(
        patch("/userinfo")
            .with(csrf())
            .contentType(APPLICATION_JSON)
            .content(
                """
                {
                  "firstName": "Integration %s",
                  "lastName": "Tests %s",
                  "nickname": "tasktracker-api-integration-tests %s"
                }
                """
                    .formatted(random, random, random)));

    mockMvc
        .perform(get("/userinfo").with(csrf()))
        .andExpect(
            content()
                .json(
                    """
                    {
                      "email": "tasktracker-api-integration-tests@dajohnston.co.uk",
                      "firstName": "Integration %s",
                      "lastName": "Tests %s",
                      "nickname": "tasktracker-api-integration-tests %s",
                      "picture": "https://s.gravatar.com/avatar/7b91293197ba56e160fa3411c320b70e?s=480&r=pg&d=https%%3A%%2F%%2Fcdn.auth0.com%%2Favatars%%2Fho.png"
                    }
                    """
                        .formatted(random, random, random)));
  }

  @Test
  @WithMockJWT(subject = "auth0|invalidid")
  void updateUserInfo_invalidUserId_returns404() throws Exception {
    mockMvc
        .perform(
            patch("/userinfo")
                .with(csrf())
                .contentType(APPLICATION_JSON)
                .content(
                    """
                {
                  "firstName": "New Name"
                }
                """))
        .andExpect(status().isNotFound());
  }
}
