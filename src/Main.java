import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import newTask.*;
import server.KVServer;
import server.adapters.InstantAdapter;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        KVServer server;
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

            server = new KVServer();
            server.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager httpTaskManager = Managers.getDefault(historyManager);

            Task task = new Task("������ ����", "������� ��������� ����.", Status.NEW,
                    Duration.ofMinutes(15), LocalDateTime.now());
            httpTaskManager.createTask(task);

            Epic epic = new Epic("���� ��������", "����������� ��� � �� ����.", Status.NEW,
                    Duration.ofMinutes(0), LocalDateTime.now());
            httpTaskManager.createEpic(epic);

            Subtask subtask1 = new Subtask("����", "�������� ����.", Status.NEW, Duration.ofMinutes(40),
                    LocalDateTime.now(), epic.getId());
            httpTaskManager.createSubtask(subtask1);

            Subtask subtask2 = new Subtask("���������", "�������� ��� � ������� �����������.",
                    Status.NEW, Duration.ofMinutes(30), LocalDateTime.now(), epic.getId());
            httpTaskManager.createSubtask(subtask2);

            httpTaskManager.getTaskById(task.getId());
            httpTaskManager.getEpicById(epic.getId());
            httpTaskManager.getSubtaskById(subtask1.getId());
            httpTaskManager.getSubtaskById(subtask2.getId());

            System.out.println("��� ������: ");
            System.out.println(gson.toJson(httpTaskManager.getAllTasks().toString()));
            System.out.println("��� �����: ");
            System.out.println(gson.toJson(httpTaskManager.getAllEpics().toString()));
            System.out.println("��� ���������: ");
            System.out.println(gson.toJson(httpTaskManager.getAllSubtasks().toString()));
            System.out.println("����������� ��������: ");
            System.out.println(httpTaskManager);
            server.stop();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}