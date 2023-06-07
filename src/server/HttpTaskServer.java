package server;

import adapter.CollectionAdapter;
import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.HttpMethod;
import model.SubTask;
import model.Task;
import service.HTTP_Manager.HTTPTaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(List.class, new CollectionAdapter())
            .create();

    public HttpTaskServer(HTTPTaskManager manager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/", new TasksHandler(manager));
        httpServer.createContext("/tasks/task/", new TaskHandler(manager));
        httpServer.createContext("/tasks/task/?id=%d", new TaskHandler(manager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(manager));
        httpServer.createContext("/tasks/epic/?id=%d", new EpicHandler(manager));
        httpServer.createContext("/tasks/subtask/", new SubTaskHandler(manager));
        httpServer.createContext("/tasks/subtask/?id=%d", new SubTaskHandler(manager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(manager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes());
    }

    protected void sendResponse(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes();
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
    }

    class TaskHandler implements HttpHandler {
        private final HTTPTaskManager manager;

        public TaskHandler(HTTPTaskManager manager) {
            this.manager = manager;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();
                System.out.println("Обрабатываю метод " + method + " по запросу " + path + ".");

                switch (HttpMethod.valueOf(method)) {
                    case GET:
                        String query = exchange.getRequestURI().getQuery();
                        if (query == null) {
                            sendResponse(exchange, gson.toJson(manager.getTaskList()), 200);
                        } else {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=")) + 3);
                            try {
                                sendResponse(exchange, gson.toJson(manager.getTask(id)), 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Не могу найти такую задачу.", 404);
                            }
                        }
                        break;
                    case POST:
                        String request = readText(exchange);
                        Task task = gson.fromJson(request, Task.class);
                        boolean idExists = manager.getTasks().containsKey(task.getId());
                        try {
                            if (idExists) {
                                manager.updateTask(task);
                                sendResponse(exchange, gson.toJson(new ArrayList<>(manager.getTasks().values())), 200);
                            } else {
                                manager.createTask(task);
                                sendResponse(exchange, gson.toJson(new ArrayList<>(manager.getTasks().values())), 200);
                            }
                        } catch (Exception e) {
                            sendResponse(exchange, "Не удается создать задачу или обновить.", 400);
                        }
                        break;
                    case DELETE:
                        query = exchange.getRequestURI().getQuery();
                        if (query == null) {
                            try {
                                manager.deleteAllTasks();
                                sendResponse(exchange, "Все задачи удалены.", 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Такой задачи нету", 404);
                            }
                        } else {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            try {
                                manager.deleteTask(id);
                                sendResponse(exchange, "Задача удалена.", 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Такой задачи нету", 404);
                            }
                        }
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
    }

    public class TasksHandler implements HttpHandler {
        private final HTTPTaskManager manager;

        public TasksHandler(HTTPTaskManager manager) {
            this.manager = manager;
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

    public class SubTaskHandler implements HttpHandler {
        private final HTTPTaskManager manager;

        public SubTaskHandler(HTTPTaskManager manager) {
            this.manager = manager;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();
                System.out.println("Обрабатываю метод " + method + " по запросу " + path + ".");

                switch (HttpMethod.valueOf(method)) {
                    case GET:
                        String query = exchange.getRequestURI().getQuery();
                        if (query == null) {
                            sendResponse(exchange, gson.toJson(manager.getSubTasksList()), 200);
                        } else {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            try {
                                sendResponse(exchange, gson.toJson(manager.getSubTask(id)), 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Не могу найти такую задачу.", 404);
                            }
                        }
                        break;
                    case POST:
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
                        break;
                    case DELETE:
                        query = exchange.getRequestURI().getQuery();
                        if (query != null) {
                            try {
                                int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                                manager.deleteSubTask(id);
                                sendResponse(exchange, "Задача с id " + id + " удалена.", 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Такой задачи нету", 404);
                            }
                        }
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
    }

    public class EpicHandler implements HttpHandler {
        private final HTTPTaskManager manager;

        public EpicHandler(HTTPTaskManager manager) {
            this.manager = manager;
        }

        @Override
        public void handle(HttpExchange exchange) {
            try {
                String path = exchange.getRequestURI().getPath();
                String method = exchange.getRequestMethod();
                System.out.println("Обрабатываю метод " + method + " по запросу " + path + ".");

                switch (HttpMethod.valueOf(method)) {
                    case GET:
                        String query = exchange.getRequestURI().getQuery();
                        if (query == null) {
                            sendResponse(exchange, gson.toJson(manager.getEpicList()), 200);
                        } else {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            try {
                                sendResponse(exchange, gson.toJson(manager.getEpic(id)), 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Не могу найти такую задачу.", 404);
                            }
                        }
                        break;
                    case POST:
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
                        break;
                    case DELETE:
                        query = exchange.getRequestURI().getQuery();
                        if (query == null) {
                            try {
                                manager.deleteAllEpic();
                                sendResponse(exchange, "Все задачи удалены.", 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Не могу найти такую задачу.", 404);
                            }
                        } else {
                            int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                            try {
                                manager.deleteEpic(id);
                                sendResponse(exchange, "Задача с id " + id + " удалена.", 200);
                            } catch (IOException e) {
                                sendResponse(exchange, "Не могу найти такую задачу.", 404);
                            }
                        }
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
    }

    public class HistoryHandler implements HttpHandler {
        private final HTTPTaskManager manager;

        public HistoryHandler(HTTPTaskManager manager) {
            this.manager = manager;
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
}