package taskManager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    int id = 1;
    protected HashMap<Integer, Task> taskList = new HashMap<>();
    protected HashMap<Integer, Epic> epicList = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public int createTask(Task task) {
        int id = generateId();
        taskList.put(id, task);
        task.setId(id);
        return id;
    }

    public int createEpic(Epic epic) {
        int id = generateId();
        epicList.put(id, epic);
        epic.setId(id);
        return id;
    }

    public int createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        Epic epic = epicList.get(subtask.getEpicId());
        if (epic != null) {
            subtaskList.put(id, subtask);
            epic.addSubtask(subtask);
            epic.setEpicStatus();
            return id;
        } else {
            return -1;
        }
    }

    public int generateId() {
        return id++;
    }

    public Task getTaskById(int id) {
        return taskList.get(id);
    }

    public Epic getEpicByID(int id) {
        return epicList.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskList.get(id);
    }

    public List<Task> getAllTasks() {
        if (taskList.isEmpty()) {
            System.out.println("Task list is empty.");
            return null;
        }
        return new ArrayList<>(taskList.values());
    }

    public List<Epic> getAllEpics() {
        if (epicList.isEmpty()) {
            System.out.println("Epic list is empty.");
            return null;
        }
        return new ArrayList<>(epicList.values());
    }

    public List<Subtask> getAllSubtasks() {
        if (subtaskList.isEmpty()) {
            System.out.println("Subtasks list is empty.");
            return null;
        }
        return new ArrayList<>(subtaskList.values());
    }

    public List<Subtask> getEpicSubtasksById(int id) {
        if (epicList.containsKey(id)) {
            Epic epic = epicList.get(id);
            return epic.getSubtasksList();
        }
        return null;
    }

    public void deleteTaskById(int id) {
        if (taskList.containsKey(id)) {
            taskList.remove(id);
            System.out.println("Task deleted.");
        } else {
            System.out.println("Task not found.");
        }
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    public void deleteEpicById(int id) {
        if (epicList.containsKey(id)) {
            Epic epic = epicList.get(id);
            epic.deleteAllSubtask();
            epicList.remove(id);
            System.out.println("Epic deleted.");
        } else {
            System.out.println("Epic not found.");
        }
    }

    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        if (subtask != null) {
            Epic epic = epicList.get(subtask.getEpicId());
            epic.deleteSubtask(subtask);
            subtaskList.remove(id);
            epic.setEpicStatus();
        } else {
            System.out.println("Subtask not found.");
        }
    }

    public void deleteAllSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubtask();
        }
    }

    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Task not found.");
        }
    }

    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Epic not found.");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskList.containsKey(subtask.getId())) {
            subtaskList.put(subtask.getId(), subtask);
            Epic epic = epicList.get(subtask.getEpicId());
            epic.setEpicStatus();
        } else {
            System.out.println("Subtask not found.");
        }
    }

    public void printAllTask() {
        if (taskList.isEmpty()) {
            System.out.println("Task list is empty.");
        } else {
            for (Task task : taskList.values()) {
                System.out.println("Task{ +" +
                        "description='" + task.getDescription() + +'\'' +
                        ", id=" + task.getId() +
                        ", name='" + task.getName() + '\'' +
                        ", status=" + task.getStatus() +
                        '}');

            }
        }
    }

    public void printAllEpic() {
        if (epicList.isEmpty()) {
            System.out.println("Epic list is empty.");
        } else {
            for (Epic epic : epicList.values()) {
                System.out.println("Epic{ +" +
                        "description='" + epic.getDescription() + +'\'' +
                        ", id=" + epic.getId() +
                        ", name='" + epic.getName() + '\'' +
                        ", status=" + epic.getStatus() +
                        '}');

            }
        }
    }

    public void printAllSubtask() {
        if (subtaskList.isEmpty()) {
            System.out.println("Subtask list is empty.");
        } else {
            for (Subtask subtask : subtaskList.values()) {
                System.out.println("Subtask{ +" +
                        "epicId=" + subtask.getEpicId() +
                        "description='" + subtask.getDescription() + +'\'' +
                        ", id=" + subtask.getId() +
                        ", name='" + subtask.getName() + '\'' +
                        ", status=" + subtask.getStatus() +
                        '}');

            }
        }
    }


}
