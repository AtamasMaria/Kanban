package manager;

import manager.TaskManager;
import org.junit.jupiter.api.Test;
import newTask.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

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

    @Test
    void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertTrue(epic.getSubtasksIdList().isEmpty());
        assertEquals(List.of(epic), epics);
    }

    @Test
    void shouldCreateSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertNotNull(subtask.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId(), "Epic's ids are different");
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtasksIdList());
    }

    @Test
    void shouldReturnNullWhenCreateTaskNull() {
        Task task = manager.createTask(null);
        assertNull(task);
    }

    @Test
    void shouldReturnNullWhenCreateEpicNull() {
        Epic epic = manager.createEpic(null);
        assertNull(epic);
    }

    @Test
    void shouldReturnNullWhenCreateSubtaskNull() {
        Subtask subtask = manager.createSubtask(null);
        assertNull(subtask);
    }

    @Test
    void shouldGetTaskByCorrectId() {
        Task task = createTask();
        task.setId(task.getId());
        manager.createTask(task);
        Epic epic = createEpic();
        epic.setId(epic.getId());
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        subtask.setId(subtask.getId());
        manager.createSubtask(subtask);
        assertEquals(task, manager.getTaskById(1));
        assertEquals(epic, manager.getEpicById(2));
        assertEquals(subtask, manager.getSubtaskById(3));
    }

    @Test
    void shouldGetTasksWithIncorrectId() {
        Task task = createTask();
        manager.createTask(task);
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        assertNull(manager.getTaskById(453));
        assertNull(manager.getEpicById(235));
        assertNull(manager.getSubtaskById(353));
    }

    @Test
    void shouldGetAllTasksWithOutTasks() {
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteTaskByCorrectId() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(task.getId());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteTaskWithIncorrectId() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTaskById(412);
        assertFalse(manager.getAllTasks().isEmpty());
    }

    @Test
    void shouldDeleteEpicByCorrectId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpicById(epic.getId());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void shouldDeleteEpicWithIncorrectId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteEpicById(634);
        assertFalse(manager.getAllEpics().isEmpty());
    }

    @Test
    void shouldDeleteSubtaskByCorrectId() {
        Subtask subtask = createSubtask(createEpic());
        manager.createSubtask(subtask);
        manager.deleteSubtaskById(subtask.getId());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteSubtaskWithIncorrectId() {
        Subtask subtask = createSubtask(createEpic());
        manager.createSubtask(subtask);
        manager.deleteSubtaskById(863);
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    void shouldDeleteEpicWithSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        epic.deleteAllSubtask();
        assertTrue(epic.getSubtasksIdList().isEmpty());
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(Status.DONE);
        manager.updateTask(task);
        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.updateEpic(null);
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.updateSubtask(null);
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    void shouldGetHistory() {
        Task task = createTask();
        manager.createTask(task);
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());
        assertEquals(3, manager.getHistory().size());
    }

    @Test
    void shouldGetHistoryWithInCorrectedTasks() {
        Task task = createTask();
        manager.createTask(task);
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.getTaskById(task.getId());
        manager.getEpicById(645);
        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void shouldUpdateEpicTimeWithNormalSubtaskList() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStartTime(LocalDateTime.now());
        subtask.setEndTime(subtask.getStartTime().plusMinutes(4));
        Subtask subtask2 = createSubtask(epic);
        manager.createSubtask(subtask2);
        subtask2.setStartTime(LocalDateTime.now().plusMinutes(5));
        subtask2.setEndTime(subtask2.getStartTime().plusMinutes(10));
        manager.updateEpic(epic);
        assertEquals(epic.getStartTime(), subtask.getStartTime());
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
    }

    @Test
    void shouldNotUpdateEpicWithEmptySubtasksList() { // Не уверенна, что выполнила правильно(
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertThrows(NullPointerException.class, () -> {manager.updateEpic(epic);},"The list of subtasks is empty");
        }
}