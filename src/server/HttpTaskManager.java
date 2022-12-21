package server;

import com.google.gson.reflect.TypeToken;
import exception.ManagerSaveException;
import manager.FileBackedTasksManager;
import com.google.gson.*;
import manager.HistoryManager;
import newTask.*;
import server.adapters.InstantAdapter;
import server.adapters.LocalDateTimeAdapter;
import taskType.TaskType;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String url) {
        client = new KVTaskClient(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        load();
    }

    @Override
    public void save() {
        List<Task> tasksList = new ArrayList<>();
        List<Epic> epicsList = new ArrayList<>();
        List<Subtask> subtasksList = new ArrayList<>();

        for (Task task : getAllTasks()) {
            tasksList.add(task);
        }
        for (Epic epic : getAllEpics()) {
            epicsList.add(epic);
        }
        for (Subtask subtask : getAllSubtasks()) {
            subtasksList.add(subtask);
        }

        client.put("tasks", gson.toJson(tasksList));
        client.put("epics", gson.toJson(epicsList));
        client.put("subtasks", gson.toJson(subtasksList));
        client.put("history", gson.toJson(getHistory()));
    }

    private void load() {
        List<Task> tasks = gson.fromJson(client.load("tasks"),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        if (tasks != null) {
            for (Task task : tasks) {
                createTask(task);
            }
        }
        List<Epic> epics = gson.fromJson(client.load("epics"), // эпики
                new TypeToken<ArrayList<Epic>>() {
                }.getType());
        if (epics != null) {
            for (Epic epic : epics) {
                createEpic(epic);
            }
        }
        List<Subtask> subtasks = gson.fromJson(client.load("subtasks"), // подзадачи
                new TypeToken<ArrayList<Subtask>>() {
                }.getType());
        if (subtasks != null) {
            for (Subtask subtask : subtasks) {
                createSubtask(subtask);
            }
        }
        List<Task> history = gson.fromJson(client.load("history"), //история просмотров
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        if (history != null) {
            for (Task task : history) {
                if (task.getType() == TaskType.EPIC) {
                    createEpic((Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    createSubtask((Subtask) task);
                } else {
                    createTask(task);
                }
            }
        }
    }
}
