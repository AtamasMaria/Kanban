package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import task.*;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int generateId() {
        return id++;
    }

    @Override
    public int createTask(Task task) {
        int id = generateId();
        taskList.put(id, task);
        task.setId(id);
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = generateId();
        epicList.put(id, epic);
        epic.setId(id);
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        Epic epic = epicList.get(subtask.getEpicId());
        if (epic != null) {
            subtaskList.put(id, subtask);
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
            return id;
        } else {
            return -1;
        }
    }


    @Override
    public Task getTaskById(int id) {
        historyManager.addTaskToHistory(taskList.get(id));
        return taskList.get(id);
    }

    @Override
    public Epic getEpicByID(int id) {
        historyManager.addTaskToHistory(epicList.get(id));
        return epicList.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.addTaskToHistory(subtaskList.get(id));
        return subtaskList.get(id);
    }

    @Override
    public List<Task> getAllTasks() {
        if (taskList.isEmpty()) {
            System.out.println("Task list is empty.");
            return null;
        }
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epicList.isEmpty()) {
            System.out.println("Epic list is empty.");
            return null;
        }
        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtaskList.isEmpty()) {
            System.out.println("Subtasks list is empty.");
            return null;
        }
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public Subtask getEpicSubtasksById(int id) {
        if (epicList.containsKey(id)) {
            Epic epic = epicList.get(id);
            for (int i = 0; i < epic.getSubtasksIdList().size(); i++) {
                Subtask subtask = subtaskList.get(epic.getSubtasksIdList().get(i));
                return subtask;
            }
        }
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        if (taskList.containsKey(id)) {
            taskList.remove(id);
            System.out.println("Task deleted.");
        } else {
            System.out.println("Task not found.");
        }
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    @Override
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

    @Override
    public void deleteAllEpics() {
        subtaskList.clear();
        epicList.clear();
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        if (subtask != null) {
            Epic epic = epicList.get(subtask.getEpicId());
            epic.deleteSubtask(id);
            subtaskList.remove(id);
            updateEpicStatus(epic);
        } else {
            System.out.println("Subtask not found.");
        }
    }

    @Override
    public void deleteAllSubtasks() {
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            epic.deleteAllSubtask();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Task not found.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Epic not found.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskList.containsKey(subtask.getId())) {
            subtaskList.put(subtask.getId(), subtask);
            Epic epic = epicList.get(subtask.getEpicId());
            updateEpicStatus(epic);
        } else {
            System.out.println("Subtask not found.");
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void updateEpicStatus(Epic epic){
        if (epicList.containsKey(epic.getId())) {
            if (epic.getSubtasksIdList().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                int sumStatusNew = 0;
                int sumStatusDone = 0;
                for (int i = 0; i < epic.getSubtasksIdList().size(); i++) {
                    Subtask subtask = subtaskList.get(epic.getSubtasksIdList().get(i));
                    if (subtask.getStatus() == Status.NEW) {
                        sumStatusNew++;
                    } else if (subtask.getStatus() == Status.DONE) {
                        sumStatusDone++;
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
                if (sumStatusNew == epic.getSubtasksIdList().size()) {
                    epic.setStatus(Status.NEW);
                } else if (sumStatusDone == epic.getSubtasksIdList().size()) {
                    epic.setStatus(Status.DONE);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Epic not found.");
        }
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
}