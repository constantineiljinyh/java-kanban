package handlers;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.HttpMethod;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;
import java.util.ArrayList;


public class EpicHandler extends AbstractHandler {

    public EpicHandler(HTTPTaskManager manager) {
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
            sendResponse(exchange, gson.toJson(manager.getEpicList()), 200);
        } else {
            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 2));
            try {
                sendResponse(exchange, gson.toJson(manager.getEpic(id)), 200);
            } catch (IOException e) {
                sendResponse(exchange, "Не могу найти такую задачу.", 404);
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String request = readText(exchange);
        Epic epic = gson.fromJson(request, Epic.class);
        boolean idExists = manager.getEpics().containsKey(epic.getId());
        try {
            if (idExists) {
                manager.updateEpic(epic);
                sendResponse(exchange, gson.toJson(new ArrayList<>(manager.getEpics().values())), 200);
            } else {
                manager.createEpic(epic);
                sendResponse(exchange, gson.toJson(new ArrayList<>(manager.getEpics().values())), 200);
            }
        } catch (Exception e) {
            sendResponse(exchange, "Не удается создать задачу или обновить.", 400);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            try {
                manager.deleteAllEpic();
                sendResponse(exchange, "Все задачи удалены.", 200);
            } catch (IOException e) {
                sendResponse(exchange, "Не могу найти такую задачу.", 404);
            }
        } else {
            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 2));
            try {
                manager.deleteEpic(id);
                sendResponse(exchange, "Задача с id " + id + " удалена.", 200);
            } catch (IOException e) {
                sendResponse(exchange, "Не могу найти такую задачу.", 404);
            }
        }
    }
}
