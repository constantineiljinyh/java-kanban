package model;

import java.time.LocalDateTime;
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

    public SubTask( String name, String description, Status status, int duration, LocalDateTime startTime,int subEpicId){
        super(name,description,status,duration,startTime);
        this.epicId = subEpicId;
    }

    public SubTask(int id, String name, Status status, String description, int duration, LocalDateTime startTime,LocalDateTime endTime,int subEpicId){
        super(id,name,status,description,duration,startTime,endTime);
        this.epicId = subEpicId;
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
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}