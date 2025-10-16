package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubtaskHandler extends http.handler.BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new Gson();

    public SubtaskHandler(TaskManager manager) {
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
            List<Subtask> subtasks = manager.getAllSubtasks();
            sendText(h, gson.toJson(subtasks), 200);
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Subtask subtask = manager.getSubtask(id);
            if (subtask != null) {
                sendText(h, gson.toJson(subtask), 200);
            } else {
                sendNotFound(h);
            }
        } else if (query.startsWith("epicId=")) {
            int epicId = Integer.parseInt(query.substring(7));
            ArrayList<Subtask> subs = manager.getSubtasksOfEpic(epicId);
            sendText(h, gson.toJson(subs), 200);
        } else {
            sendNotFound(h);
        }
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (manager.getSubtask(subtask.getId()) == null) {
                boolean created = manager.createSubtask(subtask);
                if (created) {
                    sendText(h, "{\"status\":\"subtask created\"}", 201);
                } else {
                    sendNotFound(h); // эпик не найден
                }
            } else {
                manager.updateSubtask(subtask);
                sendText(h, "{\"status\":\"subtask updated\"}", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(h);
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        String query = h.getRequestURI().getQuery();

        if (query == null) {
            manager.deleleAllSubtasks();
            sendText(h, "{\"status\":\"all subtasks deleted\"}", 200);
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.deleteSubtaskById(id);
            sendText(h, "{\"status\":\"subtask deleted\"}", 200);
        } else {
            sendNotFound(h);
        }
    }
}