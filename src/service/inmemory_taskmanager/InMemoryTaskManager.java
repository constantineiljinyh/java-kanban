package service.inmemory_taskmanager;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskType;
import service.Managers;
import service.TaskManager;
import service.history_manager.HistoryManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected final HistoryManager historyManager;
    protected static int id = 1;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistoryTasks() {
        return historyManager.getHistoryTasks();
    }

    @Override
    public int createTask(Task task) {
        task.setEndTime();
        checkTaskOverlap(task);
        task.setId(id++);
        task.setType(TaskType.TASK);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        updateDurationAndStartTimes(epic);
        epic.setId(id++);
        epic.setType(TaskType.EPIC);
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setEndTime();
            checkTaskOverlap(subTask);
            subTask.setId(id++);
            subTask.setType(TaskType.SUBTASK);
            subTasks.put(subTask.getId(), subTask);
            prioritizedTasks.add(subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.setSubTaskIdList(subTask.getId());
            updateDurationAndStartTimes(epic);
            updateEpicStatus(epic);
        } else {
            throw new IllegalArgumentException("Epic с заданным идентификатором не существует");
        }
        return subTask.getId();
    }

    @Override
    public void deleteTask(int id) {
        Task taskToRemove = tasks.remove(id);
        if (taskToRemove == null) {
            throw new IllegalArgumentException("Task с id=" + id + " не существует.");
        }
        prioritizedTasks.remove(taskToRemove);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove == null) {
            throw new IllegalArgumentException("Epic с id=" + id + " не существует.");
        }
        for (int subTaskThisEpic : epicToRemove.getSubTaskIdList()) {
            SubTask subTaskToRemove = subTasks.remove(subTaskThisEpic);
            prioritizedTasks.remove(subTaskToRemove);
            historyManager.remove(subTaskThisEpic);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTaskToRemove = subTasks.remove(id);
        if (subTaskToRemove == null) {
            throw new IllegalArgumentException("SubTask с id=" + id + " не существует.");
        }
        int epicThisSubtask = subTaskToRemove.getEpicId();

        epics.get(epicThisSubtask).getSubTaskIdList().remove(Integer.valueOf(id));
        prioritizedTasks.remove(subTaskToRemove);
        historyManager.remove(id);
        updateEpicStatus(epics.get(epicThisSubtask));
        updateDurationAndStartTimes(epics.get(epicThisSubtask));
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            throw new IllegalArgumentException("Task с таким id не существует.");
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) == null) {
            throw new IllegalArgumentException("Epic с таким id не существует.");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTasks.get(id) == null) {
            throw new IllegalArgumentException("SubTask с таким id не существует.");
        }
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Task не может быть null");
        }
        if (tasks.containsKey(task.getId())) {
            task.setEndTime();
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
            tasks.put(task.getId(), task);
        } else {
            throw new IllegalArgumentException("Такого Task не существует");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new NullPointerException("Epic не может быть null");
        }
        if (epics.containsKey(epic.getId())) {
            final Epic savedEpic = epics.get(epic.getId());
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
        } else {
            throw new IllegalArgumentException("Такого Epic не существует");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            throw new NullPointerException("SubTask не может быть null");
        }
        if (subTasks.containsKey(subTask.getId())) {
            subTask.setEndTime();
            prioritizedTasks.remove(subTasks.get(subTask.getId()));
            prioritizedTasks.add(subTask);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());

            updateEpicStatus(epic);
            updateDurationAndStartTimes(epic);
        } else {
            throw new IllegalArgumentException("Такого SubTask не существует");
        }
    }

    @Override
    public Collection<Task> getTaskList() {
        for (Integer addIndexHistory : tasks.keySet()) {
            historyManager.add(tasks.get(addIndexHistory));
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Collection<Epic> getEpicList() {
        for (Integer addIndexHistory : epics.keySet()) {
            historyManager.add(epics.get(addIndexHistory));
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public Collection<SubTask> getSubTasksList() {
        for (Integer addIndexHistory : subTasks.keySet()) {
            historyManager.add(subTasks.get(addIndexHistory));
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer deleteIndexHistory : tasks.keySet()) {
            historyManager.remove(deleteIndexHistory);
            prioritizedTasks.remove(tasks.get(deleteIndexHistory));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        deleteAllSubTasks();
        for (Integer deleteIndexHistory : epics.keySet()) {
            historyManager.remove(deleteIndexHistory);
        }
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer epic : epics.keySet()) {
            epics.get(epic).getSubTaskIdList().clear();
            updateEpicStatus(epics.get(epic));
            updateDurationAndStartTimes(epics.get(epic));
        }
        for (Integer deleteIndexHistory : subTasks.keySet()) {
            historyManager.remove(deleteIndexHistory);
            prioritizedTasks.remove(subTasks.get(deleteIndexHistory));
        }
        subTasks.clear();
    }

    @Override
    public Collection<SubTask> getSubTasks(Epic epic) {
        ArrayList<SubTask> tool = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (epic.getSubTaskIdList().contains(subTask.getId())) {
                tool.add(subTask);
            }
        }
        return tool;
    }

    private void updateEpicStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubTaskIdList().isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                int finishDone = 0;
                int finishNew = 0;
                for (int i = 0; i < epic.getSubTaskIdList().size(); i++) {
                    if (subTasks.get(epic.getSubTaskIdList().get(i)).getStatus().equals(Status.DONE)) {
                        finishDone++;
                    } else if (subTasks.get(epic.getSubTaskIdList().get(i)).getStatus().equals(Status.NEW)) {
                        finishNew++;
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
                if (finishDone == epic.getSubTaskIdList().size()) {
                    epic.setStatus(Status.DONE);
                } else if (finishNew == epic.getSubTaskIdList().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Ошибка: Эпик не найден.");
        }
    }

    private void updateDurationAndStartTimes(Epic epic) {
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        boolean hasSubtasks = false;
        for (int subTaskId : subTasks.keySet()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getEpicId() == epic.getId()) {
                if (subTask.getStartTime().isBefore(start)) {
                    start = subTask.getStartTime();
                }
                LocalDateTime subTaskEndTime = subTask.getStartTime().plusMinutes(subTask.getDuration());
                if (subTaskEndTime.isAfter(end)) {
                    end = subTaskEndTime;
                }
                hasSubtasks = true;
            }
        }
        if (hasSubtasks) {
            epic.setDuration((int) start.until(end, ChronoUnit.MINUTES));
            epic.setStartTime(start);
            epic.setEndTime(end);
        } else {
            epic.setDuration(0);
            epic.setStartTime(null);
            epic.setEndTime(null);
        }
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void checkTaskOverlap(Task task) {
        for (Task t : prioritizedTasks) {
            if (t.getId() != task.getId()) {
                LocalDateTime tStartTime = t.getStartTime();
                LocalDateTime tEndTime = t.getEndTime();
                LocalDateTime taskStartTime = task.getStartTime();
                LocalDateTime taskEndTime = task.getEndTime();
                if ((taskStartTime.isAfter(tStartTime) && taskStartTime.isBefore(tEndTime))
                        || (taskEndTime.isAfter(tStartTime) && taskEndTime.isBefore(tEndTime))
                        || (taskStartTime.isBefore(tStartTime) && taskEndTime.isAfter(tEndTime))) {
                    throw new IllegalArgumentException("Время задач пересекается.");
                }
            }
        }
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

}

