package server;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import exception.ManagerSaveException;
import newTask.*;
import server.adapters.LocalDateTimeAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public class HttpTaskServer {
    private HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager, int port) throws IOException {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks/", new TasksHandler());
        httpServer.createContext("/tasks/task/", new TaskHandler());
        httpServer.createContext("/tasks/epic/", new EpicHandler());
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler());
        httpServer.createContext("/tasks/history/", new HistoryHandler());
    }
    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }


    public class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                    exchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    response = "Некорректный запрос";
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }

    class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllTasks());
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            int id = Integer.parseInt(query.split("=")[1]);
                            Task task = taskManager.getTaskById(id);
                            response = gson.toJson(task);
                            exchange.sendResponseHeaders(200, 0);
                        } catch (NumberFormatException ex) {
                            response = "";
                            exchange.sendResponseHeaders(400, 0);
                        } catch (ManagerSaveException exception) {
                            response = "";
                            exchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Task task = gson.fromJson(bodyRequest, Task.class);
                        int id = task.getId();
                        if (taskManager.getTaskById(id) == null) {
                            Task newTask = taskManager.createTask(task);
                            int idNewTask = newTask.getId();
                            response = "Создана новая задача с id=" + idNewTask;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.updateTask(task);
                            response = "Задача с id=" + id + "обновлена.";
                            exchange.sendResponseHeaders(201, 0);
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        exchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        taskManager.deleteAllTasks();
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            int id = Integer.parseInt(query.split("=")[1]);
                            taskManager.deleteTaskById(id);
                            exchange.sendResponseHeaders(200, 0);
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутстует id";
                            exchange.sendResponseHeaders(400, 0);
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                default:
                    response = "Некорректный запрос. ";
                    exchange.sendResponseHeaders(400, 0);
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }

    class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllEpics());
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            int id = Integer.parseInt(query.split("=")[1]);
                            Epic epic = taskManager.getEpicById(id);
                            if (epic == null) {
                                response = "Эпик с таким id не найден";
                                exchange.sendResponseHeaders(404, 0);
                            } else {
                                response = gson.toJson(epic);
                                exchange.sendResponseHeaders(200, 0);
                            }
                        } catch (NumberFormatException ex) {
                            response = "";
                            exchange.sendResponseHeaders(400, 0);
                        } catch (ManagerSaveException exception) {
                            response = "";
                            exchange.sendResponseHeaders(404, 0);
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Epic epic = gson.fromJson(bodyRequest, Epic.class);
                        int id = epic.getId();
                        if (taskManager.getTaskById(id) == null) {
                            Epic newEpic = taskManager.createEpic(epic);
                            int idNewEpic = newEpic.getId();
                            response = "Создана новая задача с id=" + idNewEpic;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.updateEpic(epic);
                            response = "Задача с id=" + id + "обновлена.";
                            exchange.sendResponseHeaders(201, 0);
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        exchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        taskManager.deleteAllEpics();
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            int id = Integer.parseInt(query.split("=")[1]);
                            taskManager.deleteEpicById(id);
                            exchange.sendResponseHeaders(200, 0);
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутстует id";
                            exchange.sendResponseHeaders(400, 0);
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                default:
                    response = "Некорректный запрос. ";
                    exchange.sendResponseHeaders(400, 0);
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }

    class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        response = gson.toJson(taskManager.getAllSubtasks());
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            int id = Integer.parseInt(query.split("=")[1]);
                            Subtask subtask = taskManager.getSubtaskById(id);
                            if (subtask != null) {
                                response = gson.toJson(subtask);
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                response = "Подзадача с данным id не найдена";
                                exchange.sendResponseHeaders(400, 0);
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутстует id";
                            exchange.sendResponseHeaders(400, 0);
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "POST":
                    String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    try {
                        Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
                        int id = subtask.getId();
                        if (taskManager.getSubtaskById(id) == null) {
                            Subtask newSubtask = taskManager.createSubtask(subtask);
                            int idNewSubtask = newSubtask.getId();
                            response = "Создана новая подзадача с id=" + idNewSubtask;
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            taskManager.updateSubtask(subtask);
                            response = "Подзадача с id=" + id + "обновлена.";
                            exchange.sendResponseHeaders(201, 0);
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        exchange.sendResponseHeaders(400, 0);
                    }
                    break;
                case "DELETE":
                    query = exchange.getRequestURI().getQuery();
                    if (query == null) {
                        taskManager.deleteAllSubtasks();
                        exchange.sendResponseHeaders(200, 0);
                    } else {
                        try {
                            int id = Integer.parseInt(query.split("=")[1]);
                            taskManager.deleteSubtaskById(id);
                            exchange.sendResponseHeaders(200, 0);
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутстует id";
                            exchange.sendResponseHeaders(400, 0);
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            exchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                default:
                    response = "Некорректный запрос. ";
                    exchange.sendResponseHeaders(400, 0);
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }

    class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    response = gson.toJson(taskManager.getHistory());
                    exchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    response = "Некорректный запрос";
                    exchange.sendResponseHeaders(400, 0);
            }
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes());
            }
        }
    }
}





