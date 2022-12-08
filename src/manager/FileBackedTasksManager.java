package manager;

import exception.ManagerSaveException;
import newTask.Epic;
import newTask.Status;
import newTask.Subtask;
import newTask.Task;
import taskType.TaskType;

import java.io.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static FileBackedTasksManager loadFromFile(File file) {
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTasksManager fbtm = new FileBackedTasksManager(historyManager, file);
        try {
            String readString = Files.readString(file.toPath());
            String[] split = readString.split("\n ");
            String[] strings = split[0].split("\n");
            for (String string : strings) {
                if (string.isBlank()) {
                    break;
                } else {
                    Task task = FileDataConverter.fromString(string);

                    if (task.getType() == TaskType.EPIC) {
                        fbtm.createEpic((Epic) task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        fbtm.createSubtask((Subtask) task);
                    } else {
                        fbtm.createTask(task);
                    }
                }
            }
            if (strings.length > 2) {
                if (strings[strings.length - 2].isBlank()) {
                    for (int id : historyFromString(strings[strings.length - 1])) {
                        fbtm.addToHistory(id);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось считать данные из файла. ");
        }
        return fbtm;

    }

    public void save() {

        try {
            FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8);
            for (Task task : getAllTasks()) {
                fileWriter.write(task.toString() + "\n");
            }

            for (Epic epic : getAllEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                fileWriter.write(subtask.toString() + "\n");
            }

            fileWriter.write("\n");
            fileWriter.write(historyToString(getHistoryManager()));
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить файл");
        }
    }


    public static String historyToString(HistoryManager manager) {
        List<Task> historyTask = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        if (historyTask.isEmpty()) {
            return "";
        }
        for (Task task : historyTask) {
            sb.append(task.getId()).append(",");
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> listOfIds = new LinkedList<>();
        if (value != null) {
            String[] listId = value.split(",");
            for (String id : listId) {
                listOfIds.add(Integer.parseInt(id));
            }
            return listOfIds;
        }
        return listOfIds;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
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
        manager.createTask(task1);
        Task task2 = new Task("Купить продукты", "Заказать продукты на доставку.", Status.NEW);
        manager.createTask(task2);

        Epic epic1 = new Epic("День рождения", "Подготовить все к др сына.", Status.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Торт", "Заказать торт.", Status.NEW, epic1.getId());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Вечеринка", "Украсить дом и выслать приглашения.", Status.NEW, epic1.getId());
        manager.createSubtask(subtask2);

        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        System.out.println(manager.getHistory());
    }
}
