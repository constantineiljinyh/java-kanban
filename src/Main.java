import server.HttpTaskServer;
import server.KVServer;
import server.KVTaskClient;
import service.HTTP_Manager.HTTPTaskManager;
import service.Managers;
import service.history_manager.HistoryManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        final String uri = "http://localhost:" + KVServer.PORT;
        KVServer kvServer = new KVServer();
        kvServer.start();
        KVTaskClient client = new KVTaskClient();
        HTTPTaskManager httpTaskManager = new HTTPTaskManager();
        HttpTaskServer taskServer = new HttpTaskServer(httpTaskManager);
        taskServer.start();
        HistoryManager historyManager = Managers.getDefaultHistory();
    }
}
