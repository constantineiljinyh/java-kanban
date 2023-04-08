import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        TaskManager manager = Managers.getDefault();
        Scanner scanner = new Scanner(System.in);

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
        Epic epic2 = new Epic("Поездка", "Собраться");

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 1);
        SubTask subTask2 = new SubTask("билеты", "купить билеты", Status.NEW, 1);
        SubTask subTask3 = new SubTask("Собраться", "собрать чемодан", Status.NEW, 1);

        Epic epic3 = new Epic("ggg", "идем в кино в воскресенье", 1);
        Epic epic4 = new Epic("tttt", "Собраться", 2);
        SubTask subTask4 = new SubTask("кино", "выбрать кино", Status.DONE, 3, 1);
        SubTask subTask5 = new SubTask("билеты", "купить билеты", Status.DONE, 4, 1);
        SubTask subTask6 = new SubTask("Собраться", "собрать чемодан", Status.DONE, 5, 1);
        Task task = new Task("111", "222", Status.NEW);


        while (true) {

            int i = scanner.nextInt();
            switch (i) {
                case 1:
                    manager.createEpic(epic1);
                    manager.createEpic(epic2);
                    manager.createSubTask(subTask1);
                    manager.createSubTask(subTask2);
                    manager.createSubTask(subTask3);
                    break;
                case 2:
                    System.out.println(manager.getEpic(2));
                    manager.getEpic(1);
                    manager.getSubTask(5);
                    manager.getSubTask(4);
                    manager.getSubTask(3);
                    break;
                case 3:
                    manager.getSubTasksList();
//                  manager.deleteSubTask(3);
//                  manager.updateSubTask(subTask5);
//                  manager.updateSubTask(subTask6);
//                  manager.updateEpic(epic3);
//                  manager.updateEpic(epic4);
                    break;
                case 4:
                    // manager.deleteTask(1);
                    System.out.println(manager.getEpic(1));
                    manager.getEpic(2);
                    manager.getSubTask(3);
                    manager.getSubTask(4);
                    manager.getSubTask(5);

//                  manager.deleteAllSubTasks();
                    break;
                case 5:
                    System.out.println(manager.getHistoryTasks());
                    break;
                case 6:
             //       manager.loadFromFile("File/history.csv");
                    break;
            }
        }
    }
}
