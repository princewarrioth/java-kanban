package model;

import status.Status;
import status.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.type = Type.EPIC;
    }

    public void calculateTimeAndDuration(List<Subtask> allSubtasks) {
        if (allSubtasks.isEmpty()) {
            duration = Duration.ZERO;
            startTime = null;
            endTime = null;
            return;
        }

        Duration total = Duration.ZERO;
        LocalDateTime minStart = null;
        LocalDateTime maxEnd = null;

        for (Subtask sub : allSubtasks) {
            if (sub.getStartTime() != null && sub.getDuration() != null) {
                total = total.plus(sub.getDuration());
                if (minStart == null || sub.getStartTime().isBefore(minStart)) {
                    minStart = sub.getStartTime();
                }
                LocalDateTime end = sub.getEndTime();
                if (maxEnd == null || (end != null && end.isAfter(maxEnd))) {
                    maxEnd = end;
                }
            }
        }

        this.duration = total;
        this.startTime = minStart;
        this.endTime = maxEnd;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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

    @Override
    public Type getType() {
        return Type.EPIC;
    }
}
