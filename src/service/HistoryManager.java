package service;

import model.Epic;
import model.SubTask;
import model.Task;
import java.util.List;

public interface HistoryManager {

     void add(Task task);

     void remove(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(SubTask subtask);

    List<Task> getHistoryTasks();
}
