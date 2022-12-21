package server;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerTest;
import newTask.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private static KVServer server;
    private static TaskManager manager;

    protected Task createTask() {
        return new Task("nameTask", "TaskDescription", Status.NEW, Duration.ofMinutes(0),
                LocalDateTime.now());
    }

    protected Epic createEpic() {
        return new Epic("nameEpic", "EpicDescription", Status.NEW, Duration.ofMinutes(0),
                LocalDateTime.now());
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("nameSubtask", "SubtaskDescription", Status.NEW, Duration.ofMinutes(0),
                LocalDateTime.now(), epic.getId());
    }

    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        this.manager = Managers.getDefault(historyManager);
        server = new KVServer();
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void shouldLoadTasks() {
        Task task1 = createTask();
        manager.createTask(task1);
        manager.getTaskById(task1.getId());
        Task task2 = createTask();
        manager.createTask(task2);
        manager.getTaskById(task2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllTasks(), list);
    }

    @Test
    public void shouldLoadEpics() {
        Epic epic1 = createEpic();
        manager.createEpic(epic1);
        manager.getEpicById(epic1.getId());
        Epic epic2 = createEpic();
        manager.createEpic(epic2);
        manager.getEpicById(epic2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllEpics(), list);
    }

    @Test
    public void shouldLoadSubtasks() {
        Epic epic = createEpic();
        Subtask subtask1 = createSubtask(epic);
        manager.createSubtask(subtask1);
        manager.getSubtaskById(subtask1.getId());
        Subtask subtask2 = createSubtask(epic);
        manager.createSubtask(subtask2);
        manager.getSubtaskById(subtask2.getId());
        List<Task> list = manager.getHistory();
        assertEquals(manager.getAllSubtasks(), list);
    }
}
