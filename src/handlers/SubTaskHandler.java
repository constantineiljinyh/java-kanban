package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.HttpMethod;
import model.SubTask;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;
import java.util.ArrayList;

public class SubTaskHandler extends AbstractHandler {

    public SubTaskHandler(HTTPTaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            System.out.println("Обрабатываю метод " + method + " по запросу " + path + ".");

            switch (HttpMethod.valueOf(method)) {
                case GET:
                    handleGetRequest(exchange);
                    break;
                case POST:
                    handlePostRequest(exchange);
                    break;
                case DELETE:
                    handleDeleteRequest(exchange);
                    break;
                default:
                    sendResponse(exchange, "Такого метода не существует", 405);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            sendResponse(exchange, gson.toJson(manager.getSubTasksList()), 200);
        } else {
            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 2));
            try {
                sendResponse(exchange, gson.toJson(manager.getSubTask(id)), 200);
            } catch (IOException e) {
                sendResponse(exchange, "Не могу найти такую задачу.", 404);
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String request = readText(exchange);
        SubTask sub = gson.fromJson(request, SubTask.class);
        boolean idExists = manager.getTasks().containsKey(sub.getId());
        try {
            if (idExists) {
                manager.updateSubTask(sub);
                sendResponse(exchange, gson.toJson(new ArrayList<>(manager.getSubTasks().values())), 200);
            } else {
                manager.createSubTask(sub);
                sendResponse(exchange, gson.toJson(new ArrayList<>(manager.getSubTasks().values())), 200);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Не удается создать задачу или обновить.", 400);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            try {
                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 2));
                manager.deleteSubTask(id);
                sendResponse(exchange, "Задача с id " + id + " удалена.", 200);
            } catch (IOException e) {
                sendResponse(exchange, "Такой задачи нету", 404);
            }
        }
    }
}
