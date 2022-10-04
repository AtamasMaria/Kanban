package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtasksList = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.status = Status.NEW;
    }

    public void addSubtask(Subtask subtask) {
        subtasksList.add(subtask);
    }

    public void deleteSubtask(Subtask subtask) {
        subtasksList.remove(subtask);
    }

    public void deleteAllSubtask() {
        subtasksList.clear();
    }

    public ArrayList<Subtask> getSubtasksList() {
        return subtasksList;
    }

    public void setSubtasksList(ArrayList<Subtask> subtasksList) {
        this.subtasksList = subtasksList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id + '\'' +
                ", status='" + status + '\'' +
                " subtasksList.size()=" + subtasksList.size() + '}';
    }

    public Status setEpicStatus() {
        int sumStatusNew = 0;
        int sumStatusDone = 0;
        for (Subtask subtask : subtasksList) {
            if (Objects.equals(subtask.status, Status.NEW)) {
                sumStatusNew++;
                if (sumStatusNew == subtasksList.size()) {
                    return Status.NEW;
                }
            } else {
                sumStatusDone++;
                if (sumStatusDone == subtasksList.size()) {
                    return Status.DONE;
                }
            }
        }
        return Status.IN_PROGRESS;
    }

}
