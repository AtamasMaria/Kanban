import manager.FileBackedTasksManager;
import manager.Managers;
import org.junit.jupiter.api.*;
import newTask.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private static final String FILE_NAME = "test.csv";
    Path path = Path.of(FILE_NAME);
    File file = new File(String.valueOf(path));

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(Path.of(FILE_NAME));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldSaveAndLoad() {
        Task task = new Task("Task", "TaskDescription", Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        manager.createTask(task);
        Epic epic = new Epic("Epic", "EpicDescription", Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
        manager.createEpic(epic);
        manager.loadFromFile(file);
        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        manager.save();
        manager.loadFromFile(file);
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        manager.save();
        manager.loadFromFile(file);
        assertTrue(manager.getHistory().isEmpty());
    }


}
