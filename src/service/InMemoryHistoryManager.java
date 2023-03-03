package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> historyManager = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        historyManager.add(task);
        if (historyManager.size() > 10) {
            historyManager.remove(0);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        historyManager.add(epic);
        if (historyManager.size() > 10) {
            historyManager.remove(0);
        }
    }

    @Override
    public void addSubtask(SubTask subtask) {
        historyManager.add(subtask);
        if (historyManager.size() > 10) {
            historyManager.remove(0);
        }
    }

    @Override
    public List<Task> getHistoryTasks() {
        return historyManager;
    }
}
