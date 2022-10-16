import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

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
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(6);
        taskManager.getEpicByID(3);
        System.out.println(taskManager.getHistory());



   /*     List<Task> tasks = taskManager.getAllTasks();
        System.out.println(tasks);
        List<Epic> epics = taskManager.getAllEpics();
        System.out.println(epics);
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        System.out.println(subtasks);

        Subtask subtasksByEpic = taskManager.getEpicSubtasksById(3);
        System.out.println(subtasksByEpic);

        Task task1 = taskManager.getTaskById(1);
        task1.setStatus(Status.DONE);
        Task task2 = taskManager.getTaskById(2);
        task2.setStatus(Status.IN_PROGRESS);

        Subtask subtask = taskManager.getSubtaskById(5);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        Epic epic = taskManager.getEpicByID(3);
        System.out.println(epic);

        taskManager.deleteTaskById(1);
        taskManager.printAllTask();
        taskManager.deleteEpicById(4);
        taskManager.printAllEpic(); */

    }
}
