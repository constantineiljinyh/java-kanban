package model;

import model.Epic;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubTask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    void getTask(int id);

    void getEpic(int id);

    void getSubTask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void taskList();

    void epicList();

    void subTasksList();

    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSubTasks();

    ArrayList<SubTask> getSubTaskSpecificEpic(Epic epic);
}
