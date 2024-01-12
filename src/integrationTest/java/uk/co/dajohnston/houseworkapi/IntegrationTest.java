package uk.co.dajohnston.houseworkapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void test() throws Exception {
    mockMvc.perform(post("/tasks").content("{\"name\": \"DJ\"}")).andExpect(status().isCreated());
    mockMvc.perform(get("/tasks")).andExpect(status().isOk());
  }
}
