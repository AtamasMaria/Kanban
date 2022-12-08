package manager;

import newTask.Epic;
import newTask.Status;
import newTask.Subtask;
import newTask.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class FileDataConverter {

    public FileDataConverter() { }

    public static Task fromString(String value){
        String[] params = value.split(",");
        if (params[1].equals("TASK")) {
            Task task = new Task(params[2], params[4], Status.valueOf(params[3].toUpperCase()),
                    Duration.parse(params[7]), LocalDateTime.parse(params[5]), LocalDateTime.parse(params[6]));
            task.setId(Integer.parseInt(params[0]));
            return task;
        } else if (params[1].equals("EPIC")) {
            Epic epic = new Epic(params[2], params[4], Status.valueOf(params[3].toUpperCase()),
                    Duration.parse(params[7]), LocalDateTime.parse(params[5]));
            epic.setId(Integer.parseInt(params[0]));
            epic.setStatus(Status.valueOf(params[3].toUpperCase()));
            return epic;
        } else if (params[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(params[2], params[4], Status.valueOf(params[3].toUpperCase()),
                    Duration.parse(params[7]), LocalDateTime.parse(params[5]), Integer.parseInt(params[8]));
            subtask.setId(Integer.parseInt(params[0]));
            return subtask;
        }
        return null;
    }
}
