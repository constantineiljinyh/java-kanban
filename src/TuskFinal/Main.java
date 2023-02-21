package TuskFinal;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scanner = new Scanner(System.in);
        Task task1 = new Task("Встать", "на работу", 1, Status.NEW);
        Task task2 = new Task("Напоминание", "дать деду таблетки", 2, Status.NEW);
        Task task3 = new Task("Встать", "на работу", 1, Status.IN_PROGRESS);
        Task task4 = new Task("Напоминание", "дать деду таблетки", 2, Status.IN_PROGRESS);

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье", 3, Status.NEW);
        Epic epic2 = new Epic("Поездка", "Собраться", 4, Status.NEW);
        Epic epic3 = new Epic("Кино", "идем в кино в воскресенье", 3, Status.NEW);
        Epic epic4 = new Epic("Поездка", "Собраться", 4, Status.NEW);

        SubTask subTask1 = new SubTask("кино", "выбрать кино", 5, Status.NEW, 3);
        SubTask subTask2 = new SubTask("билеты", "купить билеты", 6, Status.NEW, 3);
        SubTask subTask3 = new SubTask("Собраться", "собрать чемодан", 7, Status.NEW, 4);

        SubTask subTask4 = new SubTask("кино", "выбрать кино", 5, Status.IN_PROGRESS, 3);
        SubTask subTask5 = new SubTask("билеты", "купить билеты", 6, Status.IN_PROGRESS, 3);
        SubTask subTask6 = new SubTask("Собраться", "собрать чемодан", 7, Status.IN_PROGRESS, 4);


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
                    manager.updateEpic(epic3);
                    manager.updateEpic(epic4);
                    break;
                case 4:
                    manager.deleteTask(task1.id);
                    manager.deleteEpic(epic3.id);

                    break;
                case 5:
                    System.out.println(epic1);
                    break;

            }
        }
    }
}
