package resources;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.backedmanager.FileBackedTasksManager;

public class Resources extends FileBackedTasksManager {

    public static void main(String[] args)  {

        FileBackedTasksManager tasksManager = new FileBackedTasksManager();

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
        Epic epic2 = new Epic("Поездка", "Собраться");
        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 3);
        SubTask subTask2 = new SubTask("билеты", "купить билеты", Status.NEW, 3);
        SubTask subTask3 = new SubTask("Собраться", "собрать чемодан", Status.NEW, 3);
        SubTask subTask4 = new SubTask("кино", "выбрать кино", Status.DONE, 6, 3);
        SubTask subTask5 = new SubTask("кино", "выбрать кино", Status.NEW, 3);
        Task task1 = new Task("задача", "описание", Status.NEW);
        Task task2 = new Task("задача2", "описание2", Status.NEW);

        tasksManager.createTask(task1);
        tasksManager.createTask(task2);
        tasksManager.createEpic(epic1);
        tasksManager.createEpic(epic2);
        tasksManager.createSubTask(subTask1);
        tasksManager.createSubTask(subTask2);
        tasksManager.createSubTask(subTask3);

        tasksManager.getEpicList();
        tasksManager.getTaskList();
        tasksManager.getSubTasksList();
        System.out.println(tasksManager.getHistoryTasks());
        tasksManager.deleteAllEpic();

        FileBackedTasksManager tasksManager1 = FileBackedTasksManager.loadFromFile(file);

        System.out.println(tasksManager1.getHistoryTasks());
    }
}
