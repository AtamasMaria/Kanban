package manager;

import java.util.ArrayList;
import java.util.List;
import task.Task;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int LIMIT_HISTORY = 10;
    private final List<Task> taskHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory(){
        return taskHistory;
    }

    @Override
    public void addTaskToHistory(Task task){
        if (taskHistory.size() < LIMIT_HISTORY) {
            taskHistory.add(task);
        } else {
            taskHistory.remove(0);
            taskHistory.add(task);
        }
    }
}
