package handlers;

import com.sun.net.httpserver.HttpExchange;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;

public class HistoryHandler extends AbstractHandler{

    public HistoryHandler(HTTPTaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            System.out.println("Обрабатываю метод " + method + " по запросу " + path + ".");

            if ("GET".equals(method)) {
                try {
                    String historyTasks = manager.getHistoryTasks().toString();
                    sendResponse(exchange, historyTasks, 200);
                } catch (IOException e) {
                    sendResponse(exchange, "Ошибка при получении истории.", 500);
                }
            } else {
                sendResponse(exchange, "Такого метода не существует.", 405);
            }
        } catch (IOException e) {
            sendResponse(exchange, "Что-то пошло не так.", 500);
        }
    }
}

