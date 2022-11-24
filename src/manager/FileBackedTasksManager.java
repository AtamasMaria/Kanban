package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import taskType.TaskType;

import java.io.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTasksManager loadFromFile(File file) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTasksManager fbtm = new FileBackedTasksManager(historyManager);
        try {
            String readString = Files.readString(file.toPath());
            String[] split = readString.split("\n ");
            String[] strings = split[0].split("\n");
            for (String string : strings) {
                Task task = fromString(string);

                if (task instanceof Epic epic) {
                    addEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    addSubtask(subtask);
                } else {
                    addTask(task);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось считать данные из файла. ");
        }
        return fbtm;

    }

    public static String toString(Task task) { //сохранение задачи в строку
        if (task.getType() == TaskType.TASK) {
            String[] strTask = {Integer.toString(task.getId()), task.getType().toString(), task.getName(),
                    task.getStatus().toString(), task.getDescription()};
            return String.join(",", strTask);
        } else if (task.getType() == TaskType.EPIC) {
            Epic epic = (Epic) task;
            String[] strEpic = {Integer.toString(epic.getId()), epic.getType().toString(), epic.getName(),
                    epic.getStatus().toString(), epic.getDescription()};
            return String.join(",", strEpic);
        } else if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            String[] strSubtask = {Integer.toString(subtask.getId()), subtask.getType().toString(), subtask.getName(),
                    subtask.getStatus().toString(), subtask.getDescription(), Integer.toString(subtask.getEpicId())};
            return String.join(",", strSubtask);
        }
        return null;
    }

    public static Task fromString(String value) { //создание задачи из строки
        String[] params = value.split(",");
        if (params[1].equals("TASK")) {
            Task task = new Task(params[4], params[2], Status.valueOf(params[3].toUpperCase()));
            task.setId(Integer.parseInt(params[0]));
            return task;
        } else if (params[1].equals("EPIC")) {
            Epic epic = new Epic(params[4], params[2], Status.valueOf(params[3].toUpperCase()));
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(Status.valueOf(params[3].toUpperCase()));
            return epic;
        } else if (params[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(params[4], params[2], Status.valueOf(params[3].toUpperCase()), Integer.parseInt(params[5]));
            subtask.setId(Integer.parseInt(params[0]));
            return subtask;
        }
        return null;
    }

    public void save() {
        try {
            FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
            for (Task task : getAllTasks()) {
                fileWriter.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                fileWriter.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                fileWriter.write(toString(subtask) + "\n");
            }

            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл");
        }
    }


    public static String historyToString(HistoryManager manager) {
        List<Task> historyTask = manager.getHistory();
        List<String> ids = new ArrayList<>();
        while (!historyTask.isEmpty()) {
            for (Task task : historyTask) {
                ids.add(String.valueOf(task.getId()));
            }
        }
        return String.join(",", ids);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> listOfIds = new LinkedList<>();
        String[] list = value.split(",");
        for (int i = 0; i < list.length; i++) {
            listOfIds.add(Integer.parseInt(list[i]));
        }
        return listOfIds;
    }

    @Override
    public int createTask(Task task) {
        int newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public int createEpic(Epic epic) {
        int newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }


    public int addTask(Task task) {
        return createTask(task);
    }

    public int addEpic(Epic epic) {
        return createEpic(epic);
    }

    public int addSubtask(Subtask subtask) {
        return createSubtask(subtask);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void remove(int id) {
        super.remove(id);
    }
}

class Main {
    public static void main(String[] args) {
        Path path = Path.of("data.csv");
        File file = new File(String.valueOf(path));
        FileBackedTasksManager manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);

        Task task1 = new Task("Звонок маме", "Вечером позвонить маме.", Status.NEW);
        manager.addTask(task1);
        Task task2 = new Task("Купить продукты", "Заказать продукты на доставку.", Status.NEW);
        manager.addTask(task2);

        Epic epic1 = new Epic("День рождения", "Подготовить все к др сына.", Status.NEW);
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Торт", "Заказать торт.", Status.NEW, epic1.getId());
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Вечеринка", "Украсить дом и выслать приглашения.", Status.NEW, epic1.getId());
        manager.addSubtask(subtask2);

        manager.getTaskById(task2.getId());
        manager.getEpicByID(epic1.getId());
        manager.getTaskById(task1.getId());
        manager.getEpicByID(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        System.out.println(manager.getHistory());


    }
}
