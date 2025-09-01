package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        String type;
        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof  Subtask) {
            type = "SUBTASK";
        } else {
            type = "TASK";
        }

        String epicID = "";
        if (task instanceof Subtask) {
            epicID = String.valueOf(((Subtask) task).getEpicID());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                epicID
        );
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        boolean result = super.createSubtask(subtask);
        if (result) {
            save();
        }
        return result;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskByiD(int id) {
        super.deleteTaskByiD(id);
        save();
    }

    public void deleteSubtaskByiD(int id) {
        super.deleteTaskByiD(id);
        save();
    }

}
