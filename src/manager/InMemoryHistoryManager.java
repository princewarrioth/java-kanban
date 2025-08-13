package manager;

import model.Task;
import org.junit.platform.engine.support.hierarchical.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {//

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }

    private final Map<Integer, Node<Task>> nodes = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task task) {
        remove(task.getId());
        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        nodes.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            linkLast(task);
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodes.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }
}
