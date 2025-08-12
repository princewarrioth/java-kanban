package manager;

import model.Task;
import org.junit.platform.engine.support.hierarchical.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {//

    private LinkedList<Task> history = new LinkedList<>();
    Set<Task> historyAsSet = new HashSet<>(history);

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

    @Override
    public void addToHistory(Task task) {
        if(task == null) {
            return;
        }
        remove(task.getId());

        Node<Task> newNode = new Node<>(tail, task, null);
        if (tail != head) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        nodes.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodes.remove(id);
        if (node == null) {
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
