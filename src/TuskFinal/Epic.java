package TuskFinal;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskID = new ArrayList<>();

    public ArrayList<Integer> getSubTaskID() {

        return subTaskID;
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);

    }


    public void setSubTaskID(Integer subTaskID) {

        this.subTaskID.add(subTaskID);


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


