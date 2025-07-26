package manager;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LAST_TEN_ACTION = 9; // изменил на 9, нумерация с листа начинается с 0

    private List<Task> history = new ArrayList<>();

    @Override
    public void addToHistory(Task task) {
        if (task == null) {
            return;
        }
        history.add(task);
        if (history.size() > LAST_TEN_ACTION) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
