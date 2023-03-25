package service;

import model.Epic;
import model.SubTask;
import model.Status;
import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;
    private static int id = 1;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistoryTasks() {
        return historyManager.getHistoryTasks();
    }

    @Override
    public int createTask(Task task) {
        task.setId(id++);
        tasks.put(task.getId(), task);
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
            subTask.setId(id++);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.setSubTaskIdList(subTask.getId());
            updateEpicStatus(epic);
        } else {
            System.out.println("ошибка");
        }
        return subTask.getId();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        for (int subTaskThisEpic : epics.get(id).getSubTaskIdList()) {
            subTasks.remove(subTaskThisEpic);
            historyManager.remove(subTaskThisEpic);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        int epicThisSubtask = subTasks.get(id).getEpicId();

        epics.get(epicThisSubtask).getSubTaskIdList().remove(Integer.valueOf(id));
        subTasks.remove(id);
        historyManager.remove(id);
        updateEpicStatus(epics.get(epicThisSubtask));
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("ошибка");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());

            for (int subTaskIndex : oldEpic.getSubTaskIdList()) {
                epic.setSubTaskIdList(subTaskIndex);
            }
            epic.setStatus(oldEpic.getStatus());
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("ошибка");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            updateEpicStatus(epic);
        } else {
            System.out.println("ошибка");
        }
    }

    @Override
    public Collection<Task> getTaskList() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpicList() {
        return epics.values();
    }

    @Override
    public Collection<SubTask> getSubTasksList() {
        return subTasks.values();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        deleteAllSubTasks();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        if (subTasks.isEmpty()) {
            return;
        }
        for (Integer epic : epics.keySet()) {
            epics.get(epic).getSubTaskIdList().clear();
            updateEpicStatus(epics.get(epic));
        }
        subTasks.clear();
    }

    @Override
    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) {
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
}
