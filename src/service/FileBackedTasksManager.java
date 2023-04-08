package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = new File("history.csv");
    }

    //Восстановление TaskManager после запуска программы
    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        // начинаем заполнять taskManager (восстанавливать его состояние)
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String csv = Files.readString(file.toPath());
            String[] lines = csv.split(System.lineSeparator()); // Разделим файл на строки превратим в массив строк String[]
            List<Integer> history = Collections.emptyList();

            for (int i = 1; i < lines.length; i++) { // Считываем Task,Epic, SubTask
                String line = lines[i];

                if (line.isEmpty()) {
                    history = TaskManagerCSVFormatter.historyFromString(lines[i + 1]);
                    break;
                }
                Task task = TaskManagerCSVFormatter.fromString(line);
                if (task.getId() > id) {
                    id = task.getId();
                }
                if (task.getTaskType().equals(TaskType.TASK)) {
                    tasksManager.tasks.put(task.getId(), task);
                }
                if (task.getTaskType().equals(TaskType.EPIC)) {
                    tasksManager.epics.put(task.getId(), (Epic) task);
                }
                if (task.getTaskType().equals(TaskType.SUBTASK)) {
                    tasksManager.subTasks.put(task.getId(), (SubTask) task);
                }
            }
            for (Map.Entry<Integer, SubTask> entry : tasksManager.subTasks.entrySet()) {// Восстанавливаем id SubTask у Epic, тоесть к какому Epic какие сабтаски привязанны
                SubTask subTask = entry.getValue();
                Epic epic = tasksManager.epics.get(subTask.getEpicId());
                epic.setSubTaskIdList(subTask.getId());
            }
            // Восстанавливаем историю
            for (int j = 0; j < history.size();j++) {
                if(tasksManager.tasks.containsKey(history.get(j))) {
                    Task taskHistory = tasksManager.tasks.get(history.get(j));
                    tasksManager.historyManager.add(taskHistory);
                } else if (tasksManager.epics.containsKey(history.get(j))) {
                    Epic epicHistory = tasksManager.epics.get(history.get(j));
                    tasksManager.historyManager.add(epicHistory);
                } else if (tasksManager.subTasks.containsKey(history.get(j))) {
                    SubTask subTaskHistory = tasksManager.subTasks.get(history.get(j));
                    tasksManager.historyManager.add(subTaskHistory);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Операция вывода завершилась неудачно.");
        }
        return tasksManager;
    }

    public void save() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(TaskManagerCSVFormatter.getHeader());
            writer.newLine();// пишем в файл перенос строки

            for (Task task : tasks.values()) {
                writer.write(TaskManagerCSVFormatter.toString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(TaskManagerCSVFormatter.toString(epic));
                writer.newLine();
            }
            for (SubTask subTask : subTasks.values()) {
                writer.write(TaskManagerCSVFormatter.toString(subTask));
                writer.newLine();
            }
            writer.newLine();//записываем пустую строку
            writer.write(TaskManagerCSVFormatter.historyToString(historyManager)); // ввиде строки записываем historyManager
        } catch (IOException e) {
            throw new ManagerSaveException("Операция ввода завершилась неудачно.");
        }

    }

    @Override
    public int createTask(Task task) throws IOException {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) throws IOException {
        super.createEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) throws IOException {
        super.createSubTask(subTask);
        save();
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) throws IOException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws IOException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) throws IOException {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) throws IOException {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) throws IOException {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) throws IOException {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public Task getTask(int id) throws IOException {
        super.getTask(id);
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) throws IOException {
        super.getEpic(id);
        save();
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) throws IOException {
        super.getSubTask(id);
        save();
        return subTasks.get(id);
    }

    @Override
    public Collection<Task> getTaskList() throws IOException {
        super.getTaskList();
        save();
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpicList() throws IOException {
        super.getEpicList();
        save();
        return epics.values();
    }

    @Override
    public Collection<SubTask> getSubTasksList() throws IOException {
        super.getSubTasksList();
        save();
        return subTasks.values();
    }

    @Override
    public void deleteAllTasks() throws IOException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() throws IOException {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTasks() throws IOException {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) throws IOException {
        ArrayList<SubTask> tool = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (epic.getSubTaskIdList().contains(subTask.getId())) {
                tool.add(subTask);
            }
        }
        save();
        return tool;
    }

    @Override
    public List<Task> getHistoryTasks() {
        return historyManager.getHistoryTasks();
    }

    public static void main(String[] args) throws IOException {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);

        Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
        Epic epic2 = new Epic("Поездка", "Собраться");

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 3);
        SubTask subTask2 = new SubTask("билеты", "купить билеты", Status.NEW, 3);
        SubTask subTask3 = new SubTask("Собраться", "собрать чемодан", Status.NEW, 3);
        SubTask subTask4 = new SubTask("кино", "выбрать кино", Status.DONE, 6, 3);
        SubTask subTask5 = new SubTask("кино", "выбрать кино", Status.NEW, 3);
        Task task1 = new Task("задача","описание",Status.NEW);
        Task task2 = new Task("задача2","описание2",Status.NEW);

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



