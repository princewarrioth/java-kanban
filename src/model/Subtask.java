package model;

import status.Status;
import status.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, Status status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
        this.type = Type.SUBTASK;
    }

    public Subtask(int id, String name, String description, Status status, int epicID, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicID = epicID;
        this.type = Type.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }
}
