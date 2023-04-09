package service.backedmanager;

import customexceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    protected static File file;

    public FileBackedTasksManager() {
        super();
        this.file = new File("history.csv");
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] lines = reader.lines().toArray(String[]::new);

            List<Integer> history = Collections.emptyList();

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];

                if (line.isEmpty()) {
                    history = TaskManagerCSVFormatter.historyFromString(lines[i + 1]);
                    break;
                }
                Task task = TaskManagerCSVFormatter.fromString(line);
                if (task.getId() > id) {
                    id = task.getId();
                }
                if (task.getClass() == Task.class) {
                    tasksManager.tasks.put(task.getId(), task);
                }
                if (task.getClass() == Epic.class) {
                    tasksManager.epics.put(task.getId(), (Epic) task);
                }
                if (task.getClass() == SubTask.class) {
                    tasksManager.subTasks.put(task.getId(), (SubTask) task);
                }
            }
            for (Map.Entry<Integer, SubTask> entry : tasksManager.subTasks.entrySet()) {
                SubTask subTask = entry.getValue();
                Epic epic = tasksManager.epics.get(subTask.getEpicId());
                epic.setSubTaskIdList(subTask.getId());
            }

            for (int j = 0; j < history.size(); j++) {
                if (tasksManager.tasks.containsKey(history.get(j))) {
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

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(TaskManagerCSVFormatter.getHeader());
            writer.newLine();

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
            writer.newLine();
            writer.write(TaskManagerCSVFormatter.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Операция ввода завершилась неудачно.");
        }
    }

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        super.getTask(id);
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        super.getEpic(id);
        save();
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        super.getSubTask(id);
        save();
        return subTasks.get(id);
    }

    @Override
    public Collection<Task> getTaskList() {
        super.getTaskList();
        save();
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpicList() {
        super.getEpicList();
        save();
        return epics.values();
    }

    @Override
    public Collection<SubTask> getSubTasksList() {
        super.getSubTasksList();
        save();
        return subTasks.values();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) {
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
}



