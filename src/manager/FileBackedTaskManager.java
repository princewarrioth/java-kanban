package manager;

import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;
import status.Type;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.time.Duration;


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
        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicID());
        }

        String start = task.getStartTime() != null ? task.getStartTime().toString() : "";
        long duration = task.getDuration() != null ? task.getDuration().toMinutes() : 0;

        return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                start,
                duration
        );
    }

    private static Task fromString(String line) {
        String[] fields = line.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        Type type = Type.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicField = fields.length > 5 ? fields[5] : "";
        String subtasksField = fields.length > 6 ? fields[6] : "";
        LocalDateTime startTime = fields[6].isEmpty() ? null : LocalDateTime.parse(fields[6]);
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[7].isEmpty() ? "0" : fields[7]));

        switch (type) {
            case TASK:
                Task task = new Task(id, name, description, status, duration, startTime);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                if (!subtasksField.isBlank()) {
                    for (String s : subtasksField.split(";")) {
                        epic.addSubtasksId(Integer.parseInt(s.trim()));
                    }
                }
                return epic;
            case SUBTASK:
                if (epicField.isBlank()) {
                    throw new IllegalStateException("У сабтаска нет epicId в файле: " + line);
                }
                int epicId = Integer.parseInt(epicField.trim());
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                Task task = fromString(line);
                maxId = Math.max(maxId, task.getId());
                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);
                        Epic epic = manager.epics.get(subtask.getEpicID());
                        if (epic != null) {
                            epic.addSubtasksId(subtask.getId());
                        }
                        break;
                }
            }

            for (Epic epic : manager.getAllEpics()) {
                manager.updateEpicStatus(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке" + file.getName(), e);
        }
        manager.id = maxId + 1;
        return manager;
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
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleleAllSubtasks() {
        super.deleleAllSubtasks();
        save();
    }
}
