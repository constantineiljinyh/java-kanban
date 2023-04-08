package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Epic(int id, TaskType taskType, String name, Status status, String description) {
        super(id, taskType, name, status, description);
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public Integer getEpicId() {
        return null;
    }
    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSubTaskIdList(Integer subTaskId) {
        this.subTaskIdList.add(subTaskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subTaskIdList.equals(epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIdList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskId=" + subTaskIdList +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}


