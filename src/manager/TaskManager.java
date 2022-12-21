package manager;

import newTask.Epic;
import newTask.Subtask;
import newTask.Task;

import java.nio.Buffer;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getEpicSubtasksById(int id);

    void deleteTaskById(int id);

    void deleteAllTasks();

    void deleteEpicById(int id);

    void deleteAllEpics();

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    void updateTask(Task task);
    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void printAllTask();

    void printAllEpic();

    void printAllSubtask();

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();

    void remove(int id);

    List<Task> getPrioritizedTasks();
}
