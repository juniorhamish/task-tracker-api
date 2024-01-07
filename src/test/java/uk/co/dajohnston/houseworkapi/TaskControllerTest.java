package uk.co.dajohnston.houseworkapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskControllerTest {

  @Test
  void tasks_shouldReturnTaskList() {
    assertEquals(0, new TaskController().tasks().size());
  }
}