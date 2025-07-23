package model;

import manager.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import status.Status;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
        void setUp() {
            taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        }

    // Проверка на то, что нельзя эпик добавить себя в виде подзадачи
    @Test
        void epicShouldNotContainItselfAsSubtask() {
            Epic epic = new Epic("Эпик 1", "Описание");
            taskManager.createEpic(epic);
            ArrayList<Subtask> subtaskOfEpic = taskManager.getSubtasksOfEpic(epic.getId());
            for (Subtask subtask : subtaskOfEpic) {
                assertNotEquals(epic.getId(), subtask.getId(), "Эпик не должен содержать сам себя как подзадачу");
            }
        }

    // Проверка на то что сабтаск нельзя сделать своим же эпиком
    @Test
        void subtaskShouldNotBeEpic() {
            Epic epic = new Epic("Test", "Test");
            Subtask subtask = new Subtask("Подзадача", "Описание", Status.NEW, epic.getId());
            taskManager.createSubtask(subtask);
            ArrayList<Subtask> subtaskOfEpic = taskManager.getSubtasksOfEpic(subtask.getId());
            for (Subtask subtask2 : subtaskOfEpic) {
                assertNotEquals(epic.getId(), subtask2.getId(), "Поздача не может быть Эпиком.");
            }
        }

    // Два теста по проверке утиллитраного класса
    @Test
        void getDefaultHistoryShouldRetrunDefaultHistory() {
            HistoryManager historyManager = Managers.getDefaultHistory();
            assertNotNull(historyManager, "История должна быть проиницализирована");
            Task task = new Task("HistoryTask", "Test", Status.NEW);
            historyManager.addToHistory(task);
            assertEquals(1, historyManager.getHistory().size(), "История должна содержать задачу");
        }

    @Test
        void getDefaultHistoryShouldReturnTaskManager() {
            TaskManager taskManager = Managers.getDefault();
            assertNotNull(taskManager, "Таск менеджер должен быть проинициализирован");
            Task task = new Task("TestTask", "Test", Status.NEW);
            taskManager.createTask(task);
            assertTrue(taskManager.getAllTasks().contains(task), "Таск должен сохранять задачи");
            taskManager.getTask(task.getId());
            assertTrue(taskManager.getHistory().contains(task), "История должна содержать задачи");
        }

    // Проверка на равенство экземпляров класса
    @Test
        void taskWithSameIdShouldBeEqaudl() {
            Task task1 = new Task("TestTask", "Test", Status.NEW);
            task1.setId(1);
            Task task2 = new Task("TestTask", "Test", Status.NEW);
            task2.setId(1);
            assertEquals(task1, task2, "Задачи с одинаковым айди должны счиаться равными");
        }

    @Test
        void epicsWithSameIdShouldBeEqual() {
            Epic epic1 = new Epic("Эпик 1", "Описание 1");
            epic1.setId(10);
            Epic epic2 = new Epic("Эпик 2", "Описание 2");
            epic2.setId(10);
            assertEquals(epic1, epic2, "Эпик объекты с одинаковым id должны считаться равными");
    }

    @Test
        void subtasksWithSameIdShouldBeEqual() {
            Subtask subtask1 = new Subtask("Сабтаск 1", "Описание 1", Status.NEW, 10); //
            subtask1.setId(20);
            Subtask subtask2 = new Subtask("Сабтаск 2", "Описание 2", Status.NEW, 10);
            subtask2.setId(20);
            assertEquals(subtask1, subtask2, "Сабтаск объекты с одинаковым айди должны считаться равными");
        }

    @Test
        void epicAndSubtaskWithSameIdShouldNotBeEqual() {
            Epic epic = new Epic("Сабтаск", "Описание");
            epic.setId(30);
            Subtask subtask = new Subtask("Сабтаск", "Описание", Status.NEW, 10);
            subtask.setId(30);
            assertNotEquals(epic, subtask, "Эпик и Сабтаск с одинаковым айди не должны считаться равными, так как это разные типы");
        }

    // Проверка что InMemoryTaskManager добавляет задачи разного типа и может найти их по ID
    @Test
        void shouldAddAndFindDifferentTaskTypesById() {
        // Task
            Task task = new Task("TaskName", "TaskDescription", Status.NEW);
            taskManager.createTask(task);
            int taskId = task.getId();
            Task fetchedTask = taskManager.getTask(taskId);
            assertNotNull(fetchedTask, "Таск должен быть найден по айди");
            assertEquals(taskId, fetchedTask.getId(), "Айди задачи должен совпадать");

        // Epic
            Epic epic = new Epic("EpicName", "EpicDescription");
            taskManager.createEpic(epic);
            int epicId = epic.getId();
            Epic fetchedEpic = taskManager.getEpic(epicId);
            assertNotNull(fetchedEpic, "Эпик должен быть найден по айди");
            assertEquals(epicId, fetchedEpic.getId(), "Айди эпика должен совпадать");

        // Subtask
            Subtask subtask = new Subtask("SubtaskName", "SubtaskDescription", Status.NEW, epicId);
            taskManager.createSubtask(subtask);
            int subtaskId = subtask.getId();
            Subtask fetchedSubtask = taskManager.getSubtask(subtaskId);
            assertNotNull(fetchedSubtask, "Сабтаск должен быть найден по айди");
            assertEquals(subtaskId, fetchedSubtask.getId(), "Айди подзадачи должен совпадать");
            assertEquals(epicId, fetchedSubtask.getEpicID(), "Подзадача должна быть привязана к правильному Эпику");
        }
}