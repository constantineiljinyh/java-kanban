package service;


import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private int generatedId = 1;

    public int createTask(Task task) {//2.4
        task.setId(generatedId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int createEpic(Epic epic) {//2.4
        epic.setId(generatedId++);
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
        return epic.getId();
    }

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

    public void deleteTask(int id) {//2.6
        tasks.remove(id);

    }

    public void deleteEpic(int id) {//2.6
        for (int idSub : epics.get(id).getSubTaskID()) {
            subTasks.remove(idSub);
        }
        epics.remove(id);
    }

    public void deleteSubTask(int id) {//2.6
        int idEpic = subTasks.get(id).getEpicId();
        int idSubTask = epics.get(idEpic).getSubTaskID().indexOf(id);
        epics.get(idEpic).getSubTaskID().remove(idSubTask);
        subTasks.remove(id);
        updateEpicStatus(epics.get(idEpic));
    }


    public Task getTask(int id) {//2.3

        return tasks.get(id);
    }

    public Epic getEpic(int id) {//2.3
        epics.get(id);
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {//2.3
        return subTasks.get(id);
    }

    public void updateTask(Task task) {//2.5
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("ошибка");
        }
    }

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


    public void updateSubTask(SubTask subTask) {//2.5
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(subTask.getEpicId());
            epic.setSubTaskID(subTask.getId());
            updateEpicStatus(epic);
        } else {
            System.out.println("ошибка");
        }

    }

    public Object taskList() {//2.1

        return tasks.values();
    }

    public Object epicList() {//2.1

        return epics.values();
    }

    public Object subTasksList() {//2.1

        return subTasks.values();
    }

    public void deleteAllTasks() {//2.2
        tasks.clear();
    }

    public void deleteAllEpic() {//2.2
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {//2.2
        for (Integer epic : epics.keySet()) {
            epics.get(epic).getSubTaskID().clear();
            updateEpicStatus(epics.get(epic));
        }
        subTasks.clear();
    }

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
                int Done = 0;
                int New = 0;
                for (int i = 0; i < epic.getSubTaskID().size(); i++) {
                    if (subTasks.get(epic.getSubTaskID().get(i)).getStatus().equals(Status.DONE)) {
                        Done++;
                    }
                    if (subTasks.get(epic.getSubTaskID().get(i)).getStatus().equals(Status.NEW)) {
                        New++;
                    }
                    if (Done == epic.getSubTaskID().size()) {
                        epic.setStatus(Status.DONE);
                    }
                    if (New == epic.getSubTaskID().size()) {
                        epic.setStatus(Status.NEW);
                    }
                    if ((Done != epic.getSubTaskID().size()) && (New != epic.getSubTaskID().size())) {
                        epic.setStatus(Status.IN_PROGRESS);
                    }
                }
            }
        } else {
            System.out.println("ошибка");
        }

    }
}
