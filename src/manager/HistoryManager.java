package manager;

import java.util.List;
import task.Task;

public interface HistoryManager {

    List<Task> getHistory();

    void addTaskToHistory(Task task);
}
