package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.HTTP_Manager.HTTPTaskManager;

public class TasksHandler extends AbstractHandler {

    public TasksHandler(HTTPTaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            System.out.println("Обрабатываю метод " + method + " по запросу " + path + ".");

            if ("GET".equals(method)) {
                sendResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
            } else {
                System.out.println("Такой метод не обрабатывается: " + method);
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}


