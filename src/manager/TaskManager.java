package manager;

import model.Task;
import model.Epic;
import model.Subtask;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int generateID();

    // METHOD TASK

    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTask(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTaskByiD(int id);


// METHOD EPIC

    ArrayList<Epic> getAllEpics();

    void deleteAllEpic();

    Epic getEpic(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);


// METHOD SUBTASK

    ArrayList<Subtask> getAllSubtasks();

    void deleleAllSubtasks();

    Subtask getSubtask(int id);

    boolean createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    Task getTask(String name, String description);

    void updateEpicStatus(Epic epic);

    // HISTORY
    List<Task> getHistory();
}

