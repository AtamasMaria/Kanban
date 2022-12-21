
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;

import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);
        new HttpTaskServer(taskManager, 8080);
    }
}