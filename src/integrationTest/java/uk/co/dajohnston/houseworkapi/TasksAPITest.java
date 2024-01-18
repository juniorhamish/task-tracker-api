package uk.co.dajohnston.houseworkapi;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class TasksAPITest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private MongoTemplate mongoTemplate;

  @AfterEach
  void tearDown() {
    mongoTemplate.getDb().drop();
  }

  @Test
  @WithMockJWT(authorities = "SCOPE_read:tasks")
  void post_tasks_createsNewTask() throws Exception {
    mockMvc.perform(post("/tasks").content("{\"name\": \"DJ\"}")
                                  .with(csrf()))
           .andExpect(status().isCreated());

    mockMvc.perform(get("/tasks").with(csrf()))
           .andExpect(status().isOk());
  }
}
