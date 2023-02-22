import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Manager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);
        Task task1 = new Task("Встать", "на работу", Status.NEW);
        Task task2 = new Task("Напоминание", "дать деду таблетки",  Status.NEW);

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
        Epic epic2 = new Epic("Поездка", "Собраться");

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 3);
        SubTask subTask2 = new SubTask("билеты", "купить билеты",  Status.NEW, 3);
        SubTask subTask3 = new SubTask("Собраться", "собрать чемодан",  Status.NEW, 4);

        Task task3 = new Task("Встать", "на работу",1, Status.IN_PROGRESS);
        Task task4 = new Task("Напоминание", "дать деду таблетки",2,  Status.IN_PROGRESS);
        Epic epic3 = new Epic("Кино", "идем в кино в воскресенье",3);
        Epic epic4 = new Epic("Поездка", "Собраться",4);
        SubTask subTask4 = new SubTask("кино", "выбрать кино",  Status.DONE, 3,5);
        SubTask subTask5 = new SubTask("билеты", "купить билеты",  Status.DONE, 3,6);
        SubTask subTask6 = new SubTask("Собраться", "собрать чемодан",  Status.DONE, 4,7);


        while (true) {

            int i = scanner.nextInt();
            switch (i) {
                case 1:
                    manager.createTask(task1);
                    manager.createTask(task2);
                    manager.createEpic(epic1);
                    manager.createEpic(epic2);
                    manager.createSubTask(subTask1);
                   manager.createSubTask(subTask2);
                    manager.createSubTask(subTask3);
                    break;
                case 2:
                    System.out.println(manager.taskList());
                    System.out.println(manager.epicList());
                    System.out.println(manager.subTasksList());
                    break;
                case 3:
                    manager.updateTask(task3);
                    manager.updateTask(task4);
                    manager.updateSubTask(subTask4);
                    manager.updateSubTask(subTask5);
                    manager.updateSubTask(subTask6);
                   // manager.updateEpic(epic3);
                  //  manager.updateEpic(epic4);
                    break;
                case 4:
//                    manager.deleteTask(1);
//                    manager.deleteEpic(3);
                   manager.deleteSubTask(6);
//                    manager.deleteSubTask(5);
                     //
                    //manager.deleteAllSubTasks();
                    break;
                case 5:
                    System.out.println(manager.getSubTaskSpecificEpic(epic1));
                    System.out.println(manager.getSubTaskSpecificEpic(epic2));
                    break;

            }
        }
    }
}
