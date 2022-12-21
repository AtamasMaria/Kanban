import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import newTask.*;
import server.KVServer;
import server.adapters.InstantAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        KVServer server;
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .create();
            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task = new Task("Звонок маме", "Вечером позвонить маме.", Status.NEW,
                    Duration.ofMinutes(0), LocalDateTime.now());
            httpTaskManager.createTask(task);

            Epic epic = new Epic("День рождения", "Подготовить все к др сына.", Status.NEW,
                    Duration.ofMinutes(0), LocalDateTime.now());
            httpTaskManager.createEpic(epic);

            Subtask subtask1 = new Subtask("Торт", "Заказать торт.", Status.NEW, Duration.ofMinutes(0),
                    LocalDateTime.now(), epic.getId());
            httpTaskManager.createSubtask(subtask1);

            Subtask subtask2 = new Subtask("Вечеринка", "Украсить дом и выслать приглашения.",
                    Status.NEW, Duration.ofMinutes(0), LocalDateTime.now(), epic.getId());
            httpTaskManager.createSubtask(subtask2);

            httpTaskManager.getTaskById(task.getId());
            httpTaskManager.getEpicById(epic.getId());
            httpTaskManager.getSubtaskById(subtask1.getId());
            httpTaskManager.getSubtaskById(subtask2.getId());

            System.out.println("Все задачи: ");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks()));
            System.out.println("Все эпики: ");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics()));
            System.out.println("Все подзадачи: ");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks()));
            System.out.println("Загруженный менеджер: ");
            System.out.println(httpTaskManager);
            server.stop();

        } catch (JsonIOException | IOException e) {
            e.getStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}