package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private final ArrayList<Integer> subTaskID = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public ArrayList<Integer> getSubTaskID() {
        return subTaskID;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSubTaskID(Integer subTaskID) {
        this.subTaskID.add(subTaskID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subTaskID.equals(epic.subTaskID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskID);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskID=" + subTaskID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}


