package manager;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import exception.ManagerValidatorException;
import newTask.*;
import org.junit.jupiter.params.shadow.com.univocity.parsers.tsv.TsvFormat;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    protected final HashMap<Integer, Task> taskList = new HashMap<>();
    protected final HashMap<Integer, Epic> epicList = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    protected final HistoryManager historyManager;
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int generateId() {
        return id++;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        } else {
            int id = generateId();
            task.setId(id);
            checkingPriorityOfTasks();
            prioritizedTasks.add(task);
            taskList.put(id, task);
            return task;
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        } else {
            int id = generateId();
            epicList.put(id, epic);
            epic.setId(id);
            return epic;
        }
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        } else {
            int id = generateId();
            subtask.setId(id);
            Epic epic = epicList.get(subtask.getEpicId());
            if (epic != null) {
                checkingPriorityOfTasks();
                prioritizedTasks.add(subtask);
                subtaskList.put(id, subtask);
                epic.addSubtaskId(id);
                updateEpicStatus(epic);
                updateTimeEpic(epic);
                return subtask;
            } else {
                return null;
            }
        }
    }


    @Override
    public Task getTaskById(int id) {
        Task task = taskList.get(id);
        if (task != null) {
            historyManager.addTaskToHistory(taskList.get(id));
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicList.get(id);
        if (epic != null) {
            historyManager.addTaskToHistory(epicList.get(id));
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        if (subtask != null) {
            historyManager.addTaskToHistory(subtaskList.get(id));
        }
        return subtask;
    }

    @Override
    public List<Task> getAllTasks() {
        if (taskList.isEmpty()) {
            System.out.println("Task list is empty.");
            return Collections.emptyList();
        }
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epicList.isEmpty()) {
            System.out.println("Epic list is empty.");
            return Collections.emptyList();
        }
        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtaskList.isEmpty()) {
            System.out.println("Subtasks list is empty.");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public List<Subtask> getEpicSubtasksById(int id) {
        if (epicList.containsKey(id)) {
            List<Subtask> subtasks = new ArrayList<>();
            Epic epic = epicList.get(id);
            for (int i = 0; i < epic.getSubtasksIdList().size(); i++) {
                Subtask subtask = subtaskList.get(epic.getSubtasksIdList().get(i));
                subtasks.add(subtask);
            }
            return subtasks;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (taskList.containsKey(id)) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            taskList.remove(id);
            System.out.println("Task deleted.");
            historyManager.remove(id);
        } else {
            System.out.println("Task not found.");
        }
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteEpicById(int id) {
        if (epicList.containsKey(id)) {
            Epic epic = epicList.get(id);
            epic.getSubtasksIdList().forEach(subtaskId -> {
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
                subtaskList.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            epicList.remove(id);
            for (Integer subtaskId : epic.getSubtasksIdList()) {
                historyManager.remove(subtaskId);
            }
            epic.deleteAllSubtask();
            historyManager.remove(id);
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
            updateTimeEpic(epic);
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
        } else {
            System.out.println("Subtask not found.");
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicList.values()) {
            for (int subtaskId : epic.getSubtasksIdList()) {
                Subtask subtask = subtaskList.get(subtaskId);
                prioritizedTasks.remove(subtask);
            }
            epic.deleteAllSubtask();
            updateEpicStatus(epic);
            updateTimeEpic(epic);
        }
        subtaskList.clear();
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && taskList.containsKey(task.getId())) {
            checkingPriorityOfTasks();
            prioritizedTasks.add(task);
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Task not found.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
            updateEpicStatus(epic);
            updateTimeEpic(epic);
        } else {
            System.out.println("Epic not found.");
        }
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtaskList.containsKey(subtask.getId())) {
            subtaskList.put(subtask.getId(), subtask);
            Epic epic = epicList.get(subtask.getEpicId());
            updateEpicStatus(epic);
            checkingPriorityOfTasks();
            prioritizedTasks.add(subtask);
            updateTimeEpic(epic);
        } else {
            System.out.println("Subtask not found.");
        }
    }

    public void updateTimeEpic(Epic epic)  {
        List<Subtask> subtasks = getEpicSubtasksById(epic.getId());
        if (subtasks.isEmpty()) {
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            Duration duration = null;
        }
        LocalDateTime startTime = subtasks.get(0).getStartTime();
        LocalDateTime endTime = subtasks.get(0).getEndTime();
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) startTime = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(endTime)) endTime = subtask.getEndTime();
        }
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        int duration = (endTime.getMinute() - startTime.getMinute());
        epic.setDuration(Duration.ofMinutes(duration));

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
                        ", startTime=" + task.getStartTime() +
                        ", endTime=" + task.getEndTime() +
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
                        ", startTime=" + epic.getStartTime() +
                        ", endTime=" + epic.getEndTime() +
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
                        ", startTime=" + subtask.getStartTime() +
                        ", endTime=" + subtask.getEndTime() +
                        '}');

            }
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void addToHistory(int id) {
        if (taskList.containsKey(id)) {
            historyManager.addTaskToHistory(getTaskById(id));
        } else if (epicList.containsKey(id)) {
            historyManager.addTaskToHistory(getEpicById(id));
        } else if (subtaskList.containsKey(id)) {
            historyManager.addTaskToHistory(getSubtaskById(id));
        }
    }

    public List<Task> getPrioritizedTasks() {
        List<Task> tasks = prioritizedTasks.stream().collect(Collectors.toList());
        return tasks;
    }

    public void checkingPriorityOfTasks() {
        List<Task> tasks = getPrioritizedTasks();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean taskHasIntersections = checkingTimeConflict(task);
            if (taskHasIntersections) {
                throw new ManagerValidatorException(
                        "Task #" + task.getId() + " and Task #" + tasks.get(i - 1).getId() + " conflict.");
            }
        }
    }

    private boolean checkingTimeConflict(Task newTask) {
        List<Task> tasks = List.copyOf(prioritizedTasks);
        int sizeTimeNull = 0;
        if (tasks.size() > 0) {
            for (Task task : tasks) {
                if (task.getStartTime() != null && task.getEndTime() != null) {
                    if (newTask.getStartTime().isBefore(task.getStartTime())
                            && newTask.getEndTime().isBefore(task.getStartTime())) {
                        return true;
                    } else if (newTask.getStartTime().isAfter(task.getEndTime())
                            && newTask.getEndTime().isAfter(task.getEndTime())) {
                        return true;
                    }
                } else {
                    sizeTimeNull++;
                }
            }
            return sizeTimeNull == tasks.size();
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "Manager{" +
                "tasks=" + taskList +
                ", subtasks=" + subtaskList +
                ", epics=" + epicList +
                ", historyManager=" + historyManager.getHistory() +
                '}';
    }
}