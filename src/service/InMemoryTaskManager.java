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
    private static int ID = 1;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistoryTasks() {
        return historyManager.getHistoryTasks();
    }

    @Override
    public int createTask(Task task) {//2.4
        task.setId(ID++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {//2.4
        epic.setId(ID++);
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {//2.4
        if (epics.containsKey(subTask.getEpicId())) {
            subTask.setId(ID++);
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.setSubTaskID(subTask.getId());
            updateEpicStatus(epic);
        } else {
            System.out.println("ошибка");
        }
        return subTask.getId();
    }

    @Override
    public void deleteTask(int id) {//2.6
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {//2.6
        for (int idSub : epics.get(id).getSubTaskID()) {
            subTasks.remove(idSub);
            historyManager.remove(idSub);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {//2.6
        int epicThisSubtask = subTasks.get(id).getEpicId();

        epics.get(epicThisSubtask).getSubTaskID().remove(Integer.valueOf(id));
        subTasks.remove(id);
        historyManager.remove(id);
        updateEpicStatus(epics.get(epicThisSubtask));
    }

    @Override
    public Task getTask(int id) {//2.3
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {//2.3
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {//2.3
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateTask(Task task) {//2.5
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("ошибка");
        }
    }

    @Override
    public void updateEpic(Epic epic) {//2.5
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());

            for (int idSub : oldEpic.getSubTaskID()) {
                epic.setSubTaskID(idSub);
            }
            epic.setStatus(oldEpic.getStatus());
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("ошибка");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {//2.5
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            updateEpicStatus(epic);
        } else {
            System.out.println("ошибка");
        }
    }

    @Override
    public Collection<Task> getTaskList() {//2.1
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpicList() {//2.1
        return epics.values();
    }

    @Override
    public Collection<SubTask> getSubTasksList() {//2.1
        return subTasks.values();
    }

    @Override
    public void deleteAllTasks() {//2.2
        tasks.clear();
    }

    @Override
    public void deleteAllEpic() {//2.2
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {//2.2
        for (Integer epic : epics.keySet()) {
            epics.get(epic).getSubTaskID().clear();
            updateEpicStatus(epics.get(epic));
        }
        subTasks.clear();
    }

    @Override
    public ArrayList<SubTask> getSubTasksForEpic(Epic epic) {//3.1
        ArrayList<SubTask> tool = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (epic.getSubTaskID().contains(subTask.getId())) {
                tool.add(subTask);
            }
        }
        return tool;
    }

    private void updateEpicStatus(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubTaskID().isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                int finishDone = 0;
                int finishNew = 0;
                for (int i = 0; i < epic.getSubTaskID().size(); i++) {
                    if (subTasks.get(epic.getSubTaskID().get(i)).getStatus().equals(Status.DONE)) {
                        finishDone++;
                    } else if (subTasks.get(epic.getSubTaskID().get(i)).getStatus().equals(Status.NEW)) {
                        finishNew++;
                    } else {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
                if (finishDone == epic.getSubTaskID().size()) {
                    epic.setStatus(Status.DONE);
                } else if (finishNew == epic.getSubTaskID().size()) {
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
