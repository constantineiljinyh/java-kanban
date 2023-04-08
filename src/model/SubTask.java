package model;

import java.util.Objects;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, Status status, int subEpicId) {
        super(name, description, status);
        this.epicId = subEpicId;
    }

    public SubTask(String name, String description, Status status, int id, int subEpicId) {
        super(name, description, status, id);
        this.epicId = subEpicId;
    }

    public SubTask(int id,TaskType taskType,String name,Status status,String description,int epicId){
        super(id,taskType,name,status,description);
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "subEpicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}