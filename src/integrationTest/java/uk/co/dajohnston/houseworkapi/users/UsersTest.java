package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.houseworkapi.security.WithMockJWT;

@SpringBootTest
@AutoConfigureMockMvc
class UsersTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void tearDown() {
    mongoTemplate.getDb().drop();
  }

  @Test
  @WithMockJWT(authorities = {"SCOPE_create:users", "SCOPE_read:allusers"})
  void post_users_createsUser() throws Exception {
    mockMvc.perform(post("/users").content("""
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                                  .contentType(APPLICATION_JSON)
                                  .with(csrf()))
           .andExpect(status().isCreated());

    mockMvc.perform(get("/users").with(csrf()))
           .andExpect(status().isOk())
           .andExpect(content().json("""
               [
                 {
                   "firstName": "David",
                   "lastName": "Johnston",
                   "emailAddress": "david.johnston@example.com"
                 }
               ]
               """));
  }
}
