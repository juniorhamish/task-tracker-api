package uk.co.dajohnston.houseworkapi.users;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UsersService usersService;

  @Test
  void create_returnsCreatedUser() throws Exception {
    when(usersService.create(any())).thenReturn(
        new User("First", "Last", "first.last@example.com"));
    mockMvc.perform(post("/users").content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON))
           .andExpect(content().json("""
               {
                 "firstName": "First",
                 "lastName": "Last",
                 "emailAddress": "first.last@example.com"
               }
               """, true));
  }


  @Test
  void post_returns201StatusCode() throws Exception {
    mockMvc.perform(post("/users").content("""
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
  void create_usesServiceToCreateUser() throws Exception {
    mockMvc.perform(post("/users").content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON));

    verify(usersService).create(new User("David", "Johnston", "david.johnston@example.com"));
  }
}
