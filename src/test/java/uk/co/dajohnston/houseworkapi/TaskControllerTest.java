package uk.co.dajohnston.houseworkapi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskControllerTest {

  private TaskController controller;

  @BeforeEach
  void setUp() {
    controller = new TaskController();
  }

  @Test
  void tasks_shouldReturnTaskList() {
    controller.saveTask(new Task(1, "Task Name"));

    assertThat(controller.tasks(), contains(new Task(1, "Task Name")));
  }

  @Test
  void task_shouldReturnTaskWithGivenId() {
    controller.saveTask(new Task(2, "My Task"));

    assertThat(controller.task(2), is(new Task(2, "My Task")));
  }
}
