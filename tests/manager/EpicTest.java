package manager;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import newTask.Epic;
import newTask.Status;
import newTask.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class EpicTest {
    TaskManager manager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
    Epic epic = new Epic("Title", "Description", Status.NEW);

    @BeforeEach
    void beforeEach() {
        manager.createEpic(epic);
    }

    @Test
    void testEpicStatusNoSubtasks() {
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Epic's status is not NEW");
    }

    @Test
    void testEpicStatusIfAllSubtasksNew() {
        manager.createSubtask(new Subtask("nameSubtask", "SubtaskDescription", Status.NEW,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        manager.createSubtask(new Subtask("nameSubtask2", "SubtaskDescription", Status.NEW,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        Assertions.assertEquals(Status.NEW, epic.getStatus(), "Epic's status is not NEW");
    }

    @Test
    void testEpicStatusIfAllSubtasksDone() {
        manager.createSubtask(new Subtask("nameSubtask", "SubtaskDescription", Status.DONE,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        manager.createSubtask(new Subtask("nameSubtask2", "SubtaskDescription", Status.DONE,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        Assertions.assertEquals(Status.DONE, epic.getStatus(), "Epic's status is not DONE");
    }

    @Test
    void testEpicStatusWithDifferentSubtaskStatuses() {
        manager.createSubtask(new Subtask("nameSubtask", "SubtaskDescription", Status.NEW,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        manager.createSubtask(new Subtask("nameSubtask2", "SubtaskDescription", Status.DONE,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic's status is not IN_PROGRESS");
    }

    @Test
    void testEpicStatusIfAllSubtasksInprogress() {
        manager.createSubtask(new Subtask("nameSubtask", "SubtaskDescription", Status.IN_PROGRESS,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        manager.createSubtask(new Subtask("nameSubtask2", "SubtaskDescription", Status.IN_PROGRESS,
                Duration.ofMinutes(0), LocalDateTime.now(), epic.getId()));
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic's status is not IN_PROGRESS");
    }



}