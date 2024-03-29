package newTask;

import manager.InMemoryTaskManager;
import manager.Managers;
import taskType.TaskType;

import java.time.*;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdList = new ArrayList<>();
    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subtasksIdList = new ArrayList<>();
    }

    public TaskType getType() {
        return TaskType.EPIC;
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
        return super.toString();
    }

}
