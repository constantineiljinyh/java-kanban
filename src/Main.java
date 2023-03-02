import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Scanner scanner = new Scanner(System.in);

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
        Epic epic2 = new Epic("Поездка", "Собраться");

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 1);
        SubTask subTask2 = new SubTask("билеты", "купить билеты", Status.NEW, 1);
        SubTask subTask3 = new SubTask("Собраться", "собрать чемодан", Status.NEW, 2);

        Epic epic3 = new Epic("ggg", 1);
        Epic epic4 = new Epic("tttt", 2);
        SubTask subTask4 = new SubTask("кино", "выбрать кино", Status.DONE, 1);
        SubTask subTask5 = new SubTask("билеты", "купить билеты", Status.DONE, 1);
        SubTask subTask6 = new SubTask("Собраться", "собрать чемодан", Status.DONE, 2);
        Task task = new Task("111", "222", Status.NEW);
//        manager.createTask(task);
//        manager.getTask(1);
//        manager.createEpic(epic1);
//        System.out.println(manager.getEpic(1));
//        manager.updateEpic(epic3);
//        System.out.println(manager.getEpic(1));
//        manager.createSubTask(subTask1);
//        manager.createSubTask(subTask2);
//        manager.createSubTask(subTask3);
//        System.out.println(manager.epicList());
//        manager.updateEpic(epic4);
//        manager.updateSubTask(subTask4);
//        manager.updateSubTask(subTask5);
//        manager.updateSubTask(subTask6);
//        System.out.println(manager.getEpic(1));
//        manager.deleteSubTask(2);
//        System.out.println(manager.getEpic(1));
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
                    manager.taskList();
                    manager.epicList();
                    manager.subTasksList();
                    break;
                case 3:
//                    manager.createSubTask(subTask4);
//                    manager.createSubTask(subTask5);
//                    manager.createTask(subTask6);
                    manager.updateEpic(epic3);
                    manager.updateEpic(epic4);
                    break;
                case 4:
                    // manager.deleteTask(1);
                    System.out.println(manager.getEpic(1));
                    manager.getEpic(2);
                    manager.getSubTask(3);
                    manager.getSubTask(4);
                    manager.getSubTask(5);

                    //manager.deleteAllSubTasks();
                    break;
                case 5:
                    System.out.println(manager.getHistoryTasks());
                    break;
            }
        }
    }
}
