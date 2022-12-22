package server;

import manager.FileBackedTasksManager;
import com.google.gson.*;
import manager.HistoryManager;
import newTask.*;
import server.adapters.InstantAdapter;
import server.adapters.LocalDateTimeAdapter;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(HistoryManager historyManager, String url) throws IOException, InterruptedException {
        super(historyManager);
        client = new KVTaskClient(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();

        JsonElement jsonTasks = JsonParser.parseString(client.load("tasks"));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.createTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load("epics"));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                Epic epic = gson.fromJson(jsonEpic, Epic.class);
                this.createEpic(epic);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load("subtasks"));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
                this.createSubtask(subtask);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(client.load("history"));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int taskId = jsonTaskId.getAsInt();
                if (this.subtaskList.containsKey(taskId)) {
                    this.getSubtaskById(taskId);
                } else if (this.epicList.containsKey(taskId)) {
                    this.getEpicById(taskId);
                } else if (this.taskList.containsKey(taskId)) {
                    this.getTaskById(taskId);
                }
            }
        }
    }

    @Override
    public void save() {
        List<Task> tasks = new ArrayList<>();
        List<Epic> epics = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();
        tasks.addAll(getAllTasks());
        epics.addAll(getAllEpics());
        subtasks.addAll(getAllSubtasks());
        client.put("tasks", gson.toJson(tasks.toString()));
        client.put("epics", gson.toJson(epics.toString()));
        client.put("subtasks", gson.toJson(subtasks.toString()));
        client.put("history", gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }

}

