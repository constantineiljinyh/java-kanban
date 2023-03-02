package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;
    private int generatedId = 1;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public int createTask(Task task) {//2.4
        task.setId(generatedId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {//2.4
        epic.setId(generatedId++);
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
        return epic.getId();
    }

    @Override
    public int createSubTask(SubTask subTask) {//2.4
        if (epics.containsKey(subTask.getEpicId())) {//проверить заданный в сабтаск существует
            subTask.setId(generatedId++);
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

    }

    @Override
    public void deleteEpic(int id) {//2.6
        for (int idSub : epics.get(id).getSubTaskID()) {
            subTasks.remove(idSub);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {//2.6
        int idEpic = subTasks.get(id).getEpicId();
        int idSubTask = epics.get(idEpic).getSubTaskID().indexOf(id);

        epics.get(idEpic).getSubTaskID().remove(idSubTask);
        subTasks.remove(id);
        updateEpicStatus(epics.get(idEpic));
    }

    @Override
    public void getTask(int id) {//2.3
        System.out.println(tasks.get(id));
        historyManager.addTask(tasks.get(id));
    }

    @Override
    public void getEpic(int id) {//2.3
        System.out.println(epics.get(id));
        historyManager.addEpic(epics.get(id));
    }

    @Override
    public void getSubTask(int id) {//2.3
        System.out.println(subTasks.get(id));
        historyManager.addSubtask(subTasks.get(id));
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
            if (epic.getDescription() == null) {
                epic.setDescription(oldEpic.getDescription());
            }
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
    public void taskList() {//2.1
        System.out.println(tasks.values());
    }

    @Override
    public void epicList() {//2.1
        System.out.println(epics.values());
    }

    @Override
    public void subTasksList() {//2.1
        System.out.println(subTasks.values());
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
    public ArrayList<SubTask> getSubTaskSpecificEpic(Epic epic) {//3.1
        ArrayList<SubTask> tool = new ArrayList<>();
        for (Integer epics : subTasks.keySet()) {
            for (Integer epicId : epic.getSubTaskID())
                if (epics.equals(epicId)) {
                    tool.add(subTasks.get(epics));
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
                    }
                    if (subTasks.get(epic.getSubTaskID().get(i)).getStatus().equals(Status.NEW)) {
                        finishNew++;
                    }
                    if (finishDone == epic.getSubTaskID().size()) {
                        epic.setStatus(Status.DONE);
                    }
                    if (finishNew == epic.getSubTaskID().size()) {
                        epic.setStatus(Status.NEW);
                    }
                    if ((finishDone != epic.getSubTaskID().size()) && (finishNew != epic.getSubTaskID().size())) {
                        epic.setStatus(Status.IN_PROGRESS);
                    }
                }
            }
        } else {
            System.out.println("ошибка");
        }
    }
}
