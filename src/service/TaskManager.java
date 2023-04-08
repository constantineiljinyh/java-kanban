package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {

    List<Task> getHistoryTasks() throws IOException;

    int createTask(Task task) throws IOException;

    int createEpic(Epic epic) throws IOException;

    int createSubTask(SubTask subTask) throws IOException;

    void deleteTask(int id) throws IOException;

    void deleteEpic(int id) throws IOException;

    void deleteSubTask(int id) throws IOException;

    Task getTask(int id) throws IOException;

    Epic getEpic(int id) throws IOException;

    SubTask getSubTask(int id) throws IOException;

    void updateTask(Task task) throws IOException;

    void updateEpic(Epic epic) throws IOException;

    void updateSubTask(SubTask subTask) throws IOException;

    Collection<Task> getTaskList() throws IOException;

    Collection<Epic> getEpicList() throws IOException;

    Collection<SubTask> getSubTasksList() throws IOException;

    void deleteAllTasks() throws IOException;

    void deleteAllEpic() throws IOException;

    void deleteAllSubTasks() throws IOException;

    ArrayList<SubTask> getSubTasksForEpic(Epic epic) throws IOException;

}
