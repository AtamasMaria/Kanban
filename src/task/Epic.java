package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdList = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.status = Status.NEW;
    }

    public void addSubtaskId(int id) {
        subtasksIdList.add(id);
    }

    public void deleteSubtask(int id) {
        subtasksIdList.remove(id);
    }

    public void deleteAllSubtask() {
        subtasksIdList.clear();
    }

    public ArrayList<Integer> getSubtasksIdList() {
        return subtasksIdList;
    }

    public void setSubtasksIdList(ArrayList<Integer> subtasksIdList) {
        this.subtasksIdList = subtasksIdList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() + '\'' +
                ", status='" + getStatus() + '\'' +
                " subtasksList.size()=" + subtasksIdList.size() + '}';
    }
}
