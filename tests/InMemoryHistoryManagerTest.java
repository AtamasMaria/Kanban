import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.*;
import newTask.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class  InMemoryHistoryManagerTest {
    HistoryManager manager;
    private int id = 0;

    public int generateId() {
        return ++id;
    }

    protected Task createTask() {
        return new Task("nameTask", "TaskDescription", Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
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
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldGetEmptyHistoryList() {
        List<Task> historyTasks = manager.getHistory();
        assertTrue(historyTasks.isEmpty());
    }

    @Test
    public void shouldAddTaskEpicSubtaskToHistory() {
        Task task = createTask();
        int taskId = generateId();
        task.setId(taskId);
        manager.addTaskToHistory(task);
        Epic epic = createEpic();
        int epicId = generateId();
        epic.setId(epicId);
        manager.addTaskToHistory(epic);
        Subtask subtask = createSubtask(epic);
        int subtaskId = generateId();
        subtask.setId(subtaskId);
        manager.addTaskToHistory(subtask);
        assertEquals(List.of(task, epic, subtask), manager.getHistory());


    }

    @Test// Проверка на дублирование истории задач
    public void shouldGetHistoryListNoDouble() {
        Task task = createTask();
        int task1Id = generateId();
        task.setId(task1Id);
        manager.addTaskToHistory(task);
        Epic epic = createEpic();
        int epicId = generateId();
        epic.setId(epicId);
        manager.addTaskToHistory(task);
        manager.addTaskToHistory(epic);
        assertEquals(List.of(task, epic), manager.getHistory());
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    public void shouldRemoveTask() {
        Task task1 = createTask();
        int task1Id = generateId();
        task1.setId(task1Id);
        manager.addTaskToHistory(task1);
        Task task2 = createTask();
        int task2Id = generateId();
        task2.setId(task2Id);
        manager.addTaskToHistory(task2);
        Epic epic = createEpic();
        int epicId = generateId();
        epic.setId(epicId);
        manager.addTaskToHistory(epic);
        manager.remove(epicId);
        assertEquals(2, manager.getHistory().size());
        assertEquals(List.of(task1, task2), manager.getHistory());
    }

    @Test
    public void shouldRemoveOnlyOneTask() {
        Task task = createTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        manager.addTaskToHistory(task);
        manager.remove(task.getId());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldNotRemoveTaskWithBadId() {
        Task task = createTask();
        int newTaskId = generateId();
        task.setId(newTaskId);
        manager.addTaskToHistory(task);
        manager.remove(345);
        assertEquals(List.of(task), manager.getHistory());
    }
}
