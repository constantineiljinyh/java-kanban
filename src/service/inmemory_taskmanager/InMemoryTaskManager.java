package service.inmemory_taskmanager;

import model.Epic;
import model.SubTask;
import model.Status;
import model.Task;
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
import java.util.Objects;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
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
        for (Task t : tasks.values()) {
            if (t.getStartTime().equals(task.getStartTime()) && t.getEndTime().equals(task.getEndTime())) {
                throw new IllegalArgumentException("Задача с таким же временем уже существует");
            }
        }
        task.setId(id++);
        checkTaskOverlap(task);
        tasks.put(task.getId(), task);
        getPrioritizedTasks();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(id++);
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {
        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setEndTime();
            for (SubTask t : subTasks.values()) {
                if (t.getStartTime().equals(subTask.getStartTime()) && t.getEndTime().equals(subTask.getEndTime())) {
                    throw new IllegalArgumentException("Подзадача с таким же временем уже существует");
                }
            }
            checkTaskOverlap(subTask);
            subTask.setId(id++);
            subTasks.put(subTask.getId(), subTask);
            getPrioritizedTasks();
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
        if (tasks.get(id) == null) {
            throw new IllegalArgumentException("Epic с id=" + id + " не существует.");
        }
        tasks.remove(id);
        getPrioritizedTasks();
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.get(id) == null) {
            throw new IllegalArgumentException("Task с id=" + id + " не существует.");
        }
        for (int subTaskThisEpic : epics.get(id).getSubTaskIdList()) {
            subTasks.remove(subTaskThisEpic);
            historyManager.remove(subTaskThisEpic);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (subTasks.get(id) == null) {
            throw new IllegalArgumentException("SubTask с id=" + id + " не существует.");
        }
        int epicThisSubtask = subTasks.get(id).getEpicId();

        epics.get(epicThisSubtask).getSubTaskIdList().remove(Integer.valueOf(id));
        subTasks.remove(id);
        getPrioritizedTasks();
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
            tasks.put(task.getId(), task);
            getPrioritizedTasks();
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
            Epic oldEpic = epics.get(epic.getId());

            for (int subTaskIndex : oldEpic.getSubTaskIdList()) {
                epic.setSubTaskIdList(subTaskIndex);
            }
            epic.setStatus(oldEpic.getStatus());
            epics.put(epic.getId(), epic);
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
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            getPrioritizedTasks();
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
        }
        tasks.clear();
        getPrioritizedTasks();
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
        }
        subTasks.clear();
        getPrioritizedTasks();
    }

    @Override
    public Collection<SubTask> getSubTasksForEpic(Epic epic) {
        ArrayList<SubTask> tool = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (epic.getSubTaskIdList().contains(subTask.getId())) {
                tool.add(subTask);
            }
        }
        return tool;
    }

    public void updateEpicStatus(Epic epic) {
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

    public void updateDurationAndStartTimes(Epic epic) {
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;
        for (int subTaskId : subTasks.keySet()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (earliestStartTime == null || subTask.getStartTime().isBefore(earliestStartTime)) {
                earliestStartTime = subTask.getStartTime();
            }
            LocalDateTime subTaskEndTime = subTask.getStartTime().plusMinutes(subTask.getDuration());
            if (latestEndTime == null || subTaskEndTime.isAfter(latestEndTime)) {
                latestEndTime = subTaskEndTime;
            }
        }
        if (earliestStartTime != null) {
            epic.setDuration((int)earliestStartTime.until(Objects.requireNonNull(latestEndTime), ChronoUnit.MINUTES));
        }
        epic.setStartTime(earliestStartTime);
        epic.setEndTime(latestEndTime);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        Comparator<Task> startTimeComparator = Comparator.comparing(Task::getStartTime);
        TreeSet<Task> sortedSet = new TreeSet<>(startTimeComparator);
        sortedSet.addAll(tasks.values());
        sortedSet.addAll(subTasks.values());
        return sortedSet;
    }

    public void checkTaskOverlap(Task task) {
        for (Task t : getPrioritizedTasks()) {
            if (t.getId() != task.getId()) { // исключаем проверку самой себя
                LocalDateTime tStartTime = t.getStartTime();
                LocalDateTime tEndTime = t.getEndTime();
                LocalDateTime taskStartTime = task.getStartTime();
                LocalDateTime taskEndTime = task.getEndTime();
                if ((taskStartTime.isAfter(tStartTime) && taskStartTime.isBefore(tEndTime))
                        || (taskEndTime.isAfter(tStartTime) && taskEndTime.isBefore(tEndTime))
                        || (taskStartTime.isBefore(tStartTime) && taskEndTime.isAfter(tEndTime))) {
                    throw new IllegalArgumentException("Время задач пересекается." );
                }
            }
        }
    }
}

