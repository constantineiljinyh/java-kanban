package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getHistoryTasks();

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubTask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    Collection<Task> getTaskList();

    Collection<Epic> getEpicList();

    Collection<SubTask> getSubTasksList();

    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSubTasks();

    Collection<SubTask> getSubTasksForEpic(Epic epic);
    void updateEpicStatus(Epic epic);
    TreeSet<Task> getPrioritizedTasks();

    void updateDurationAndStartTimes(Epic epic);
    void checkTaskOverlap(Task task);
}

