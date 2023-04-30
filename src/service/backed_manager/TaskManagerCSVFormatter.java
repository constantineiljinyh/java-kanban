package service.backed_manager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskType;
import service.history_manager.HistoryManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerCSVFormatter {

    public static String getHeader() {
        return "id,type,name,status,description,duration,start_time,end_time,epic";
    }

    public static String toString(Task task) {
        String type = "";
        String epicId = "";

        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }
        if (task.getClass() == Task.class) {
            type = "TASK";
        } else if (task.getClass() == Epic.class) {
            type = "EPIC";
        } else if (task.getClass() == SubTask.class) {
            type = "SUBTASK";
        }
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s,%s",
                task.getId(), type, task.getName(), task.getStatus(),
                task.getDescription(), task.getDuration(), task.getStartTime(), task.getEndTime(), epicId);
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        int duration = 0;
        if (!parts[5].isEmpty()) {
            duration = Integer.parseInt(parts[5]);
        }
        LocalDateTime startTime = null;
        if (!parts[6].equals("null") && !parts[6].isEmpty()) {
            startTime = LocalDateTime.parse(parts[6]);
        }
        LocalDateTime endTime = null;
        if (!parts[7].equals("null") && !parts[7].isEmpty()) {
            endTime = LocalDateTime.parse(parts[7]);
        }
        if (type.equals(TaskType.SUBTASK)){
            int epicId = Integer.parseInt(parts[8]);
            return new SubTask( id,  name,  status,  description,  duration,  startTime, endTime, epicId);
        }
        if (type.equals(TaskType.TASK)) {
            return new Task(id, name, status, description, duration, startTime, endTime);
        } else {
            return new Epic(id, name, status, description, duration, startTime, endTime);
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
        return historyList;
    }
}

