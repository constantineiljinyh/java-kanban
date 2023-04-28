import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        Scanner scanner = new Scanner(System.in);

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
        Epic epic2 = new Epic("Поездка", "Собраться");

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 60, LocalDateTime.of(2023, 10, 20, 12, 0), 1);
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.IN_PROGRESS, 60, LocalDateTime.of(2023, 10, 20, 13, 10), 1);


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

                    break;
                case 2:
                    System.out.println(manager.getEpic(2));
                    manager.getEpic(1);
                    manager.getSubTask(5);
                    manager.getSubTask(4);
                    manager.getSubTask(3);
                    break;
                case 3:
                    manager.deleteSubTask(3);
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
                    System.out.println(manager.getPrioritizedTasks());
                    break;
                case 6:

                    break;
            }
        }
    }
}
