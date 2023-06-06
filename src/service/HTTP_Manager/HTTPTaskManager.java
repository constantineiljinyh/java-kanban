package service.HTTP_Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import Adapters.Adapter;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;
import server.KVServer;
import server.KVTaskClient;
import service.backed_manager.FileBackedTasksManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static model.TaskType.EPIC;
import static model.TaskType.SUBTASK;
import static model.TaskType.TASK;

public class HTTPTaskManager extends FileBackedTasksManager {

    private static final String URI = "http://localhost:" + KVServer.PORT;
    private final KVTaskClient client;

    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new Adapter.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new Adapter.DurationAdapter())
            .create();

    public HTTPTaskManager(KVTaskClient client, boolean loadOnConstruct) {
        super(URI);
        this.client = client;
        if (loadOnConstruct) {
            load();
        }
    }

    public HTTPTaskManager(boolean loadOnConstruct) {
        super(URI);
        this.client = new KVTaskClient();
        if (loadOnConstruct) {
            load();
        }
    }

   public void load() {
        Map<String, String> loadedTasks = new HashMap<>();
        loadedTasks.put("tasks", client.load("/tasks/task/"));
        loadedTasks.put("epics", client.load("/tasks/epic/"));
        loadedTasks.put("subtasks", client.load("/tasks/subtask/"));
        loadedTasks.put("history", client.load("/tasks/history"));

        int maxId = 0;
        for (Map.Entry<String, String> map : loadedTasks.entrySet()) {
            JsonElement element = JsonParser.parseString(map.getValue());
            String key = map.getKey();
            if (element.isJsonArray()) {
                JsonArray jsonArray = element.getAsJsonArray();
                switch (key) {
                    case "tasks":
                        for (JsonElement elem : jsonArray) {
                            Task task = gson.fromJson(elem, Task.class);
                            tasks.put(task.getId(), task);
                            prioritizedTasks.add(task);
                            if (task.getId() > maxId) {
                                maxId = task.getId();
                            }
                        }
                        break;
                    case "epics":
                        for (JsonElement elem : jsonArray) {
                            Epic epicTask = gson.fromJson(elem, Epic.class);
                            epics.put(epicTask.getId(), epicTask);
                            if (epicTask.getId() > maxId) {
                                maxId = epicTask.getId();
                            }
                        }
                        break;
                    case "subtasks":
                        for (JsonElement elem : jsonArray) {
                            SubTask subTask = gson.fromJson(elem, SubTask.class);
                            subTasks.put(subTask.getId(), subTask);
                            prioritizedTasks.add(subTask);
                            if (subTask.getId() > maxId) {
                                maxId = subTask.getId();
                            }
                        }
                        break;
                    case "history":
                        for (JsonElement elem : jsonArray) {
                            String typeString = String.valueOf(elem.getAsJsonObject().get("type"));
                            TaskType type = TaskType.valueOf(typeString.replaceAll("\"", ""));
                            if (type.equals(TASK)) {
                                Task task = gson.fromJson(elem, Task.class);
                                this.historyManager.add(task);
                            } else if (type.equals(EPIC)) {
                                Epic epic = gson.fromJson(elem, Epic.class);
                                this.historyManager.add(epic);
                            } else if (type.equals(SUBTASK)) {
                                SubTask subTask = gson.fromJson(elem, SubTask.class);
                                this.historyManager.add(subTask);
                            }
                        }
                        break;
                }
            }
        }
        id = maxId + 1;
    }
   public void getLoad(){
        load();
   }
    @Override
    public void save() {
        client.put("/tasks/task/", gson.toJson(new ArrayList<>(this.getTasks().values())));
        client.put("/tasks/epic/", gson.toJson(new ArrayList<>(this.getEpics().values())));
        client.put("/tasks/subtask/", gson.toJson(new ArrayList<>(this.getSubTasks().values())));
        client.put("/tasks/history", gson.toJson(this.getHistoryTasks()));
    }
}