package uk.co.dajohnston.houseworkapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TaskController {

  private static final Map<Long, Task> tasks = new HashMap<>();

  @GetMapping("/tasks")
  public List<Task> tasks() {
    log.warn("Getting all tasks");
    return new ArrayList<>(tasks.values());
  }

  @GetMapping("/tasks/{id}")
  public Task task(@PathVariable long id) {
    return tasks.get(id);
  }

  @PostMapping("/tasks")
  public Task saveTask(@RequestBody Task task) {
    tasks.put(task.id(), task);
    return task;
  }
}
