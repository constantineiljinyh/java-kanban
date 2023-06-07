package server;

import adapter.CollectionAdapter;
import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {

    private HTTPTaskManager manager;
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(List.class, new CollectionAdapter())
            .create();
    protected Task task1 = new Task("задача", "описание", Status.NEW, 300, LocalDateTime.of(2024, 4, 20, 12, 12));
    protected Task task2 = new Task("задача2", "описание2", Status.NEW, 200, LocalDateTime.of(2025, 4, 20, 13, 12));
    protected Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    private static HttpClient httpClient;
    private static final String URIH = "http://localhost:8080/tasks/task/";
    private static final String URIH_EPIC = "http://localhost:8080/tasks/epic/";
    private static final String URIH_SUBTASK = "http://localhost:8080/tasks/subtask/";

    @BeforeEach
    void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpClient = HttpClient.newBuilder().build();
        manager = new HTTPTaskManager(false);

        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    @AfterEach
    void stop() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        final int id = manager.createTask(task1);
        Task task = manager.getTask(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(task, manager.getTasks().get(id));
    }

    @Test
    void createEpic() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic =  manager.getEpic(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(epic, manager.getEpics().get(id));
    }

    @Test
    void createSub() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic =  manager.getEpic(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        final int idSub = manager.createSubTask(subTask1);
        SubTask subTask =  manager.getSubTask(idSub);
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_SUBTASK))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(subTask1, manager.getSubTasks().get(idSub));
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        final int id = manager.createTask(task2);
        Task task = manager.getTask(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(task2, manager.getTasks().get(id));

        task.setStatus(Status.IN_PROGRESS);
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH + "?id=" + id))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(Status.IN_PROGRESS, manager.getTasks().get(id).getStatus());
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic1 =  manager.getEpic(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        epic1.setName("Rename");
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC + "?id=" + id))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("Rename", manager.getEpics().get(id).getName());
    }

    @Test
    void updateSab() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic =  manager.getEpic(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        final int idSub = manager.createSubTask(subTask1);
        SubTask subTask = (SubTask) manager.getSubTask(idSub);
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_SUBTASK))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        subTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask);
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_SUBTASK + "?id=" + idSub))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(Status.IN_PROGRESS, manager.getSubTasks().get(idSub).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpics().get(id).getStatus());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        final int id = manager.createTask(task1);
        Task task = manager.getTask(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(task1, manager.getTasks().get(id));

        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH + "?id=" + id))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThrows(IllegalArgumentException.class, () -> manager.getTask(id), "Task с таким id не существует.");
    }

    @Test
    void deleteAllTask() throws IOException, InterruptedException {
        final int id = manager.createTask(task1);
        Task task0 = manager.getTask(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task0)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(task0, manager.getTasks().get(id));

        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic = manager.getEpic(id);

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    void deleteSubTask() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic =  manager.getEpic(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        final int idSub = manager.createSubTask(subTask1);
        SubTask subTask =  manager.getSubTask(idSub);
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_SUBTASK))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(subTask1, manager.getSubTasks().get(idSub));

        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_SUBTASK + "?id=" + idSub))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void deleteAllEpicAndSubTask() throws IOException, InterruptedException {
        final int id = manager.createEpic(epic1);
        Epic epic =  manager.getEpic(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        final int idSub = manager.createSubTask(subTask1);
        SubTask subTask =  manager.getSubTask(idSub);
        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_SUBTASK))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(subTask1, manager.getSubTasks().get(idSub));

        request = HttpRequest.newBuilder()
                .uri(URI.create(URIH_EPIC))
                .DELETE()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    void history() throws IOException, InterruptedException {
        final int id = manager.createTask(task1);
        Task task = manager.getTask(id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URIH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(task, manager.getHistoryTasks().get(0));
    }


}