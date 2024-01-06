package uk.co.dajohnston.houseworkapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskControllerTest {

    @Test
    void tasks_shouldReturnTaskList() {
        assertEquals(new TaskController().tasks().size(), 2);
    }
}