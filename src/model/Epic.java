package model;

import status.Status;

import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description){
        super(name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskId() {
        return new ArrayList<>(subtasksId);
    }

    public void addSubtasksId(int subtasksIds) {
        subtasksId.add(subtasksIds);
    }

    public void clearSubtasks() {
        subtasksId.clear();
    }

    public void removeSubtaskId(int id) {
        subtasksId.remove((Integer) id);
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }
}
