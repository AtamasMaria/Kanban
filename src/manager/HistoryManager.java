package manager;

import java.util.List;
import newTask.Task;

public interface HistoryManager {

    List<Task> getHistory();

    void addTaskToHistory(Task task);

    void remove(int id);
}
