import manager.TaskManager;
import model.Epic;
import status.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    public void setUp() {
        manager = createManager();
    }

    // Тест статуса эпика: все подзадачи NEW
    @Test
    public void testEpicStatus_AllNew() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Desc1", Status.NEW, epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Desc2", Status.NEW, epic.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.NEW, manager.getEpic(epic.getId()).getStatus());
    }

    // Все подзадачи DONE
    @Test
    public void testEpicStatus_AllDone() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Desc1", Status.DONE, epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Desc2", Status.DONE, epic.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.DONE, manager.getEpic(epic.getId()).getStatus());
    }

    // Подзадачи NEW и DONE
    @Test
    public void testEpicStatus_MixedNewDone() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Desc1", Status.NEW, epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Desc2", Status.DONE, epic.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    // Подзадачи IN_PROGRESS
    @Test
    public void testEpicStatus_AllInProgress() {
        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        Subtask sub1 = new Subtask("Subtask1", "Desc1", Status.IN_PROGRESS, epic.getId());
        Subtask sub2 = new Subtask("Subtask2", "Desc2", Status.IN_PROGRESS, epic.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void testTaskOverlap() {
        Task task1 = new Task("Task1", "Desc1", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 10, 13, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Task2", "Desc2", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 10, 13, 10, 30));
        task2.setDuration(Duration.ofMinutes(30));


        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2));
    }

    @Test
    public void testEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryAddRemove() {
        Task task = new Task("Task1", "Desc", Status.NEW);
        manager.createTask(task);
        manager.getTask(task.getId());

        assertEquals(1, manager.getHistory().size());

        manager.deleteTaskById(task.getId());
        assertTrue(manager.getHistory().isEmpty());
    }
}