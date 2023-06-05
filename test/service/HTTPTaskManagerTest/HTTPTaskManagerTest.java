package service.HTTPTaskManagerTest;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import service.HTTP_Manager.HTTPTaskManager;
import service.Managers;
import service.TaskManagerTest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private HttpTaskServer server;
    private KVServer serverKv;
    HttpClient httpClient;

    @BeforeEach
    void start() throws IOException{
        serverKv = new KVServer();
        serverKv.start();
        httpClient = HttpClient.newBuilder().build();
        manager = (HTTPTaskManager) Managers.getDefault();
        server = new HttpTaskServer(manager);

        server.start();
    }

    @AfterEach
    void stop() {
        server.stop();
        serverKv.stop();
    }

    @Test
    void load() {

        final int id = manager.createTask(task1);
        Task task = manager.getTask(id);

        final int idEpic = manager.createEpic(epic1);
        Epic epic =  manager.getEpic(idEpic);

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.DONE, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        final int idSub1 = manager.createSubTask(subTask1);
        SubTask sub1 =  manager.getSubTask(idSub1);

        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.DONE, 12, LocalDateTime.of(2024, 10, 20, 12, 12), epic.getId());
        final int idSub2 = manager.createSubTask(subTask2);
        SubTask sub2 =  manager.getSubTask(idSub2);

        HTTPTaskManager manager1 = new HTTPTaskManager();
        manager1.load();

        Epic epic2 = manager1.getEpics().get(idEpic);
        assertEquals(task, manager1.getTasks().get(id));
        assertEquals(epic2, manager1.getEpics().get(idEpic));
        assertEquals(sub1, manager1.getSubTasks().get(idSub1));

        assertEquals(manager1.getTaskList(), manager.getTaskList(), "Список тасков не совпадает");
        assertEquals(manager1.getEpicList(), manager.getEpicList(), "Список эпиков не совпадает");
        assertEquals(manager1.getSubTasksList(), manager.getSubTasksList(), "Список сабов не совпадает");
        assertEquals(manager1.getPrioritizedTasks(), manager.getPrioritizedTasks(),
                "Список приоритетных задач не совпадает");
        assertEquals(manager1.getHistoryTasks(), manager.getHistoryTasks(), "Истории не совпадают");
    }


}
