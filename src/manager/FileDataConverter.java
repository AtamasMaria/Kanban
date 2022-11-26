package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class FileDataConverter {

    public FileDataConverter() { }

    public static Task fromString(String value){
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
}
