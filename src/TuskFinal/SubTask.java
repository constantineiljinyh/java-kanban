package TuskFinal;

class SubTask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public SubTask(String name, String description, int id, Status status, int subEpicId) {
        super(name, description, id, status);
        this.epicId = subEpicId;
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