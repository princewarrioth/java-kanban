package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 1;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int generateID() {
        return id++;
    }

    // METHOD TASK

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return tasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        int id = generateID();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTaskByiD(int id) {
        tasks.remove(id);
    }

// METHOD EPIC

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epics.get(id);
    }

    @Override
    public void createEpic(Epic epic) {
        int id = generateID();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saveEpic = epics.get(epic.getId());
        epic.setSubtasksId(saveEpic.getSubtaskId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
            }
        }
    }

// METHOD SUBTASK

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleleAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addToHistory(subtask);
        return subtasks.get(id);
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            int id = generateID();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.addSubtasksId(subtask.getId());
            updateEpicStatus(epic);
            return true;
        } else {
            System.out.println("Эпик с данным значением не найден. Подзадача не будет создана");
            return false;
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
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

    @Override
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

    @Override
    public Task getTask(String name, String description) {
        int id = generateID();
        Task task = new Task(id, name, description, Status.NEW);
        tasks.put(id, task);
        return task;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
