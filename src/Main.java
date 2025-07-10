import model.Task;
import model.Epic;
import model.Subtask;
import manager.TaskManager;
import status.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // ЗАДАЧИ
        Task task1 = new Task( "Эпик 1", "Описание задачи 1", Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task( "Эпик 2", "Описание задачи 2", Status.NEW);
        manager.createTask(task2);

        // ЭПИК 1 И РЕАЛИЗАЦИЯ ПОДЗАДАЧЕЙ
        Epic epic1 = new Epic( "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask( "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask( "Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId());
        manager.createSubtask(subtask2);

        // ЭПИК 2 И РЕАЛИЗАЦИЯ ПОДЗАДАЧЕЙ
        Epic epic2 = new Epic( "Эпик 2", "Описание эпика 2");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask(manager.generateID(), "Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getId());
        manager.createSubtask(subtask3);

        // РАСПЕЧАТЫВАНИЕ ВСЕХ ЗАДАЧЕЙ
        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getId() + " - " + task.getName() + " - " + task.getStatus());
        }

        System.out.println("Все эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.getId() + " - " + epic.getName() + " - " + epic.getStatus());
        }

        System.out.println("Все подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask.getId() + " - " + subtask.getName() + " - " + subtask.getStatus() + " (Epic ID: " + subtask.getEpicID() + ")");
        }

        // УСТАНОВКА СТАТУСА
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        // ПОСЛЕ ИЗМЕНЕНИЯ СТАТУСА
        System.out.println("После изменения статусов:");

        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getId() + " - " + task.getName() + " - " + task.getStatus());
        }

        System.out.println("Все эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.getId() + " - " + epic.getName() + " - " + epic.getStatus());
        }

        System.out.println("Все подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask.getId() + " - " + subtask.getName() + " - " + subtask.getStatus() + " (Epic ID: " + subtask.getEpicID() + ")");
        }

        manager.deleteTaskByiD(task2.getId());
        manager.deleteEpicById(epic1.getId());

        System.out.println("После удаления задачи и эпика:");

        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getId() + " - " + task.getName() + " - " + task.getStatus());
        }

        System.out.println("Все эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.getId() + " - " + epic.getName() + " - " + epic.getStatus());
        }

        System.out.println("Все подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask.getId() + " - " + subtask.getName() + " - " + subtask.getStatus() + " (Epic ID: " + subtask.getEpicID() + ")");
        }
    }
}
