import manager.HistoryManager;
import manager.Managers;
import model.Task;
import model.Epic;
import model.Subtask;
import manager.TaskManager;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // ЗАДАЧИ
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 10, 13, 9, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 10, 13, 11, 0));
        task2.setDuration(Duration.ofMinutes(30));
        manager.createTask(task2);

        // ЭПИК 1
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        subtask1.setStartTime(LocalDateTime.of(2025, 10, 13, 12, 0));
        subtask1.setDuration(Duration.ofMinutes(45));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId());
        subtask2.setStartTime(LocalDateTime.of(2025, 10, 13, 13, 0));
        subtask2.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(subtask2);

        // ЭПИК 2
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getId());
        subtask3.setStartTime(LocalDateTime.of(2025, 10, 13, 10, 0));
        subtask3.setDuration(Duration.ofMinutes(50));
        manager.createSubtask(subtask3);

        // Вывод всех задач
        System.out.println("Все задачи:");
        for (Task t : manager.getAllTasks()) {
            System.out.println(t.getId() + " - " + t.getName() + " - " + t.getStatus()
                    + " (Start: " + t.getStartTime() + ", Duration: " + t.getDuration().toMinutes() + "мин)");
        }

        System.out.println("Все эпики:");
        for (Epic e : manager.getAllEpics()) {
            System.out.println(e.getId() + " - " + e.getName() + " - " + e.getStatus()
                    + " (Start: " + e.getStartTime() + ", End: " + e.getEndTime() + ")");
        }

        System.out.println("Все подзадачи:");
        for (Subtask s : manager.getAllSubtasks()) {
            System.out.println(s.getId() + " - " + s.getName() + " - " + s.getStatus()
                    + " (Epic ID: " + s.getEpicID() + ", Start: " + s.getStartTime()
                    + ", Duration: " + s.getDuration().toMinutes() + "мин)");
        }

        // Вывод задач по приоритету
        System.out.println("\nЗадачи по приоритету:");
        for (Task t : manager.getPrioritizedTasks()) {
            System.out.println(t.getName() + " — начало: " + t.getStartTime()
                    + ", конец: " + t.getEndTime());
        }
    }
}