package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import manager.TaskManagerTest;
import newTask.*;
import org.junit.jupiter.api.*;
import server.adapters.InstantAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest<T extends TaskManagerTest<HttpTaskManager>> {
    private KVServer kvServer;
    private HttpClient client;
    private Gson gson;
    private HttpTaskServer taskServer;
    private Task task1;
    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;

    protected Task createTask() {
        return new Task("nameTask", "TaskDescription", Status.NEW, Duration.ofMinutes(0),
                LocalDateTime.now());
    }

    protected Epic createEpic() {
        return new Epic("nameEpic", "EpicDescription", Status.NEW);
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("nameSubtask", "SubtaskDescription", Status.NEW, Duration.ofMinutes(0),
                LocalDateTime.now(), epic.getId());
    }

    @BeforeEach
    public void createTaskManager() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);
        taskServer = new HttpTaskServer(taskManager, 8080);

        task1 = createTask();
        epic1 = createEpic();
        subtask1 = createSubtask(epic1);
        subtask2 = createSubtask(epic1);

    }

    @Test
    public void checkTasksEndpoint() {
        try {
            URI uri1 = URI.create("http://localhost:8080/tasks/");
            HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
            // GET
            HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertFalse(response.body().isEmpty());

            // Other
            URI uri2 = URI.create("http://localhost:8080/tasks/");
            HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).DELETE().build();
            response = client.send(request2, HttpResponse.BodyHandlers.ofString());
            assertEquals(405, response.statusCode());
            assertTrue(response.body().isEmpty());
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    @Test
    public void checkEndpointAddTask() throws IOException, InterruptedException {
        String json = gson.toJson(task1);
        URI uri1 = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());

        json = "";
        URI uri2 = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response2.statusCode());
        assertTrue(response2.body().isEmpty());
    }

    @Test
    public void checkEndpointUpdateTask() throws IOException, InterruptedException {
        String json = gson.toJson(task1);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());
        Task task = gson.fromJson(response.body(), Task.class);

        json = gson.toJson(task);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri).POST(body1).build();
        response = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());

        task1.setId(756);
        json = gson.toJson(task1);
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).POST(body2).build();
        response = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void checkEndpointGetTask() throws IOException, InterruptedException {
        String json = gson.toJson(task1);

        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);

        String url = "http://localhost:8080/tasks/task/?id=" + task.getId();
        URI uri1 = URI.create(url);
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode());
        assertFalse(response1.body().isEmpty());

        URI uri2 = URI.create("http://localhost:8080/tasks/task/?id=6446");
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri1).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
       assertTrue(response.body().isEmpty());
    }

    @Test
    public void checkEndpointPutTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("json");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void checkEndpointDeleteTask() throws IOException, InterruptedException {
        String json = gson.toJson(task1);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);

        String url = "http://localhost:8080/tasks/task/?id=" + task.getId();
        URI uri1 = URI.create(url);
        HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(uri).POST(body1).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(response.body().isEmpty());

        URI uri2 = URI.create("http://localhost:8080/tasks/task/?id=6256");
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    



}


