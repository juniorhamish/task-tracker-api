package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.houseworkapi.security.SecurityConfig;
import uk.co.dajohnston.houseworkapi.security.WithMockJWT;

@WebMvcTest(UsersController.class)
@Import(SecurityConfig.class)
class UsersAuthorizationTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UsersService usersService;

  @Test
  void post_noToken_returns401Response() throws Exception {
    mockMvc.perform(post("/users").with(csrf())
                                  .content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON))
           .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockJWT
  void post_tokenWithoutWriteUsersScope_returns403Response() throws Exception {
    mockMvc.perform(post("/users").with(csrf())
                                  .content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON))
           .andExpect(status().isForbidden());
  }

  @Test
  @WithMockJWT(authorities = "SCOPE_write:users")
  void post_tokenWithWriteUsersScope_returns201Response() throws Exception {
    mockMvc.perform(post("/users").with(csrf())
                                  .content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON))
           .andExpect(status().isCreated());
  }

  @Test
  @WithMockJWT(authorities = "SCOPE_write:users")
  void post_requestWithoutCsrfToken_returns403Response() throws Exception {
    mockMvc.perform(post("/users").content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON))
           .andExpect(status().isForbidden());
  }

  @Test
  void get_noToken_returns401Response() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockJWT
  void get_tokenWithoutReadUsersScope_returns403Response() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isForbidden());
  }

  @Test
  @WithMockJWT(authorities = "SCOPE_read:users")
  void get_tokenWithReadUsersScope_returns200Response() throws Exception {
    mockMvc.perform(get("/users"))
           .andExpect(status().isOk());
  }
}
