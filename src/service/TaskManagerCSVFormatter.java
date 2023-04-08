package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskType;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerCSVFormatter {

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(), task.getTaskType(), task.getName(), task.getStatus(),
                task.getDescription(), task.getEpicId());
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        if (!parts[5].equals("null") && !parts[5].isEmpty()) {
            int epicId = Integer.parseInt(parts[5]);
            return new SubTask(id, type, name, status, description, epicId);
        }
        if (type.equals(TaskType.TASK)) {
            return new Task(id, type, name, status, description);
        } else {
            return new Epic(id, type, name, status, description);
        }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        List<Task> historyTasks = manager.getHistoryTasks();
        for (Task task : historyTasks) {
            sb.append(task.getId()).append(",");
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] historyIds = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        for (String historyId : historyIds) {
            historyList.add(Integer.parseInt(historyId));
        }
        return historyList;              // берем строку делим по запятым и сохраняем все id которые там были
    }
}
