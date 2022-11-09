import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        System.out.println("Tests");
        taskManager.createTask(new Task("Купить пылесос.",
                "Найти и купить пылесос в интернет-магазине.", Status.NEW));
        taskManager.createTask(new Task("Купить продукты.", "Зайти вечером в магазин.", Status.NEW));
        taskManager.createEpic(new Epic("Переезд", "Переезд в новую квартиру", Status.NEW));
        taskManager.createEpic(new Epic("Машина", "Покупка новой машины", Status.NEW));
        taskManager.createSubtask(new Subtask("Собрать коробки", " ", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Вызвать грузчиков", " ", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Съездить в автосалон", " ", Status.NEW, 4));

        taskManager.getTaskById(1);
        taskManager.getEpicByID(3);
        taskManager.getEpicByID(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getEpicByID(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(6);
        taskManager.getEpicByID(3);

        List<Task> history = taskManager.getHistory();
        System.out.println(history);
        taskManager.deleteEpicById(3);
        List<Task> history2 = taskManager.getHistory();
        System.out.println(history2);
        taskManager.remove(1);
        taskManager.remove(8);
        List<Task> history3 = taskManager.getHistory();
        System.out.println(history3);
    }
}

