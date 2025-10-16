package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends http.handler.BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new Gson();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> sendText(exchange, "{\"error\":\"Unsupported method\"}", 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        String query = h.getRequestURI().getQuery();

        if (query == null) {
            List<Epic> epics = manager.getAllEpics();
            sendText(h, gson.toJson(epics), 200);
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Epic epic = manager.getEpic(id);
            if (epic != null) {
                sendText(h, gson.toJson(epic), 200);
            } else {
                sendNotFound(h);
            }
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Epic epic = gson.fromJson(body, Epic.class);

        if (manager.getEpic(epic.getId()) == null) {
            manager.createEpic(epic);
            sendText(h, "{\"status\":\"epic created\"}", 201);
        } else {
            manager.updateEpic(epic);
            sendText(h, "{\"status\":\"epic updated\"}", 201);
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        String query = h.getRequestURI().getQuery();

        if (query == null) {
            manager.deleteAllEpic();
            sendText(h, "{\"status\":\"all epics deleted\"}", 200);
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.deleteEpicById(id);
            sendText(h, "{\"status\":\"epic deleted\"}", 200);
        } else {
            sendNotFound(h);
        }
    }
}
