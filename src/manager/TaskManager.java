package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Subtask getEpicSubtasksById(int id);

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

}
