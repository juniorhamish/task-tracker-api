package uk.co.dajohnston.tasktrackerapi.users;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.tasktrackerapi.exceptions.DuplicateResourceException;
import uk.co.dajohnston.tasktrackerapi.security.SecurityConfig;
import uk.co.dajohnston.tasktrackerapi.security.WithMockJWT;
import uk.co.dajohnston.tasktrackerapi.users.controller.UserDTO;
import uk.co.dajohnston.tasktrackerapi.users.controller.UsersController;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockJWT(scope = "read:users create:users")
class UsersControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UsersService usersService;

  @Test
  void post_returnsCreatedUser() throws Exception {
    when(usersService.create(any()))
        .thenReturn(new UserDTO("First", "Last", "first.last@example.com"));

    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .content(
                    """
                    {
                      "firstName": "David",
                      "lastName": "Johnston",
                      "emailAddress": "david.johnston@example.com"
                    }
                    """)
                .contentType(APPLICATION_JSON))
        .andExpect(
            content()
                .json(
                    """
                    {
                      "firstName": "First",
                      "lastName": "Last",
                      "emailAddress": "first.last@example.com"
                    }
                    """,
                    true));
  }

  @Test
  void post_usesServiceToCreateUser() throws Exception {
    mockMvc.perform(
        post("/users")
            .with(csrf())
            .content(
                """
                {
                  "firstName": "David",
                  "lastName": "Johnston",
                  "emailAddress": "david.johnston@example.com"
                }
                """)
            .contentType(APPLICATION_JSON));

    verify(usersService).create(new UserDTO("David", "Johnston", "david.johnston@example.com"));
  }

  @Test
  void post_duplicateEmailAddress_returns409Response() throws Exception {
    when(usersService.create(any())).thenThrow(DuplicateResourceException.class);

    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .content(
                    """
                    {
                      "firstName": "David",
                      "lastName": "Johnston",
                      "emailAddress": "david.johnston@example.com"
                    }
                    """)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockJWT(
      scope = "read:users",
      roles = {"Admin"})
  void get_userHasReadUsersScopeAndAdminRole_returnsAllUsers() throws Exception {
    when(usersService.findAll())
        .thenReturn(
            List.of(
                new UserDTO("David", "Johnston", "david.johnston@example.com"),
                new UserDTO("Bobby", "Davro", "bobby.davro@example.com")));

    mockMvc
        .perform(get("/users"))
        .andExpect(
            content()
                .json(
                    """
                    [
                      {
                        "firstName": "David",
                        "lastName": "Johnston",
                        "emailAddress": "david.johnston@example.com"
                      },
                      {
                        "firstName": "Bobby",
                        "lastName": "Davro",
                        "emailAddress": "bobby.davro@example.com"
                      }
                    ]
                    """));
  }

  @Test
  @WithMockJWT(
      scope = "read:users",
      roles = {"Admin"})
  void get_userHasReadUsersScopeAndAdminRole_findsAllUsersFromService() throws Exception {
    mockMvc.perform(get("/users"));

    verify(usersService).findAll();
  }

  @Test
  @WithMockJWT(scope = "read:users")
  void get_userHasReadUsersScope_returnsScopedUsers() throws Exception {
    when(usersService.findScopedUsers(any()))
        .thenReturn(List.of(new UserDTO("David", "Johnston", "david.johnston@example.com")));

    mockMvc
        .perform(get("/users"))
        .andExpect(
            content()
                .json(
                    """
                    [
                      {
                        "firstName": "David",
                        "lastName": "Johnston",
                        "emailAddress": "david.johnston@example.com"
                      }
                    ]
                    """));
  }

  @Test
  @WithMockJWT(scope = "read:users", emailAddress = "david.johnston@example.com")
  void get_userHasReadUsersScope_findsScopedUsersFromServiceByEmailAddress() throws Exception {
    mockMvc.perform(get("/users"));

    verify(usersService).findScopedUsers("david.johnston@example.com");
  }
}
