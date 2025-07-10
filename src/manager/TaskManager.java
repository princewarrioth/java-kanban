package manager;

import model.Task;
import model.Epic;
import model.Subtask;
import status.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int id = 1;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateID() {
        return id++;
    }

    // METHOD TASK

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        int id = generateID();
        task.setId(id);
        tasks.put(id, task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteTaskByiD(int id) {
        tasks.remove(id);
    }

// METHOD EPIC

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        int id = generateID();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void updateEpic(Epic epic) {
        Epic saveEpic = epics.get(epic.getId());
        epic.setSubtasksId(saveEpic.getSubtaskId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
            }
        }
    }

// METHOD SUBTASK

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleleAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void createSubtask(Subtask subtask) {
        int id = generateID();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.addSubtasksId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
        }
    }

    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subId : epic.getSubtaskId()) {
                result.add(subtasks.get(subId));
            }
        }
        return result;
    }


    public Task getTask(String name, String description) {
        int id = generateID();
        Task task = new Task(id, name, description, Status.NEW);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpicID(String name, String description) {
        int id = generateID();
        Epic epic = new Epic(id, name, description);
        epics.put(id, epic);
        return epic;
    }

    public Subtask createSubtaskID(String name, String description, int epicID) {
        Epic epic = epics.get(epicID);
        if (epic == null) {
            System.out.println("Epic " + epicID + " not found");
            return null;
        }
        int id = generateID();
        Subtask subtask = new Subtask(id, name, description, Status.NEW, epicID);
        subtasks.put(id, subtask);
        epic.addSubtasksId(id);
        return subtask;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subIds = epic.getSubtaskId();
        if (subIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer id : subIds) {
            Status status = subtasks.get(id).getStatus();
            if (status != Status.DONE) {
                allDone = false;
            }
            if (status != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}

