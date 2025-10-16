package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 1;

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager != null ? historyManager : new InMemoryHistoryManager();
    }

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
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
        prioritizedTasks.removeIf(task -> task instanceof Task && !(task instanceof Subtask));
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return tasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        checkTaskOverlap(task);
        int id = generateID();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        checkTaskOverlap(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void deleteTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
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
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
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
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
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
            checkTaskOverlap(subtask);
            int id = generateID();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.addSubtasksId(subtask.getId());
            prioritizedTasks.add(subtask);
            updateEpicStatus(epic);
            return true;
        } else {
            System.out.println("Эпик с данным значением не найден. Подзадача не будет создана");
            return false;
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        checkTaskOverlap(subtask);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        prioritizedTasks.remove(subtask);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
            historyManager.remove(id);
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
        Task task = new Task(id, name, description, Status.NEW, Duration.ZERO, null);
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

    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(Task::getId));

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void checkTaskOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) return;

        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        boolean overlaps = prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(existing -> {
                    LocalDateTime start = existing.getStartTime();
                    LocalDateTime end = existing.getEndTime();
                    return newStart.isBefore(end) && newEnd.isAfter(start);
                });

        if (overlaps) {
            throw new IllegalArgumentException("Ошибка пересечение задач по времени");
        }
    }

}

