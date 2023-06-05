package server;

import com.sun.net.httpserver.HttpServer;
import handlers.EpicHandler;
import handlers.HistoryHandler;
import handlers.SubTaskHandler;
import handlers.TaskHandler;
import handlers.TasksHandler;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    HTTPTaskManager manager;
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer(HTTPTaskManager manager) throws IOException {
        this.manager = manager;
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/",  new TasksHandler(manager));
        httpServer.createContext("/tasks/task/",  new TaskHandler(manager));
        httpServer.createContext("/tasks/task/?id=%d", new TaskHandler(manager));
        httpServer.createContext("/tasks/epic/",  new EpicHandler(manager));
        httpServer.createContext("/tasks/epic/?id=%d",  new EpicHandler(manager));
        httpServer.createContext("/tasks/subtask/",  new SubTaskHandler(manager));
        httpServer.createContext("/tasks/subtask/?id=%d",  new SubTaskHandler(manager));
        httpServer.createContext("/tasks/history/",  new HistoryHandler(manager));

    }

    public void start() {
        httpServer.start();
    }
    public void stop() {
        httpServer.stop(1);
    }
}
