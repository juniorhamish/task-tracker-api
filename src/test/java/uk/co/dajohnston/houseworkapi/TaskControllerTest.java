package uk.co.dajohnston.houseworkapi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import org.junit.jupiter.api.Test;

class TaskControllerTest {

  @Test
  void tasks_shouldReturnTaskList() {
    assertThat(new TaskController().tasks(), is(empty()));
  }
}
