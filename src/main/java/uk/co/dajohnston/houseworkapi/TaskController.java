package uk.co.dajohnston.houseworkapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Arrays.asList;

@RestController
public class TaskController {

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/tasks")
    public List<Task> tasks() {
        return asList(new Task(counter.incrementAndGet(), "Task"), new Task(counter.incrementAndGet(), "Task"));
    }

    @GetMapping("/task/{id}")
    public Task task(@PathVariable long id) {
        return new Task(id, "Task Name");
    }
}
