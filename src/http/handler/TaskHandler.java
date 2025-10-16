package http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends http.handler.BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, (com.google.gson.JsonSerializer<Duration>) (src, typeOfSrc, context) ->
                        new com.google.gson.JsonPrimitive(src.toMillis()))
                .registerTypeAdapter(Duration.class, (com.google.gson.JsonDeserializer<Duration>) (json, typeOfT, context) ->
                        Duration.ofMillis(json.getAsLong()))
                .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        new com.google.gson.JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                        LocalDateTime.parse(json.getAsString()))
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        switch (method) {
            case "POST":
                handlePost(exchange);
                break;
            case "GET":
                handleGet(exchange, query);
                break;
            case "DELETE":
                handleDelete(exchange, query);
                break;
            default:
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes()), Task.class);
        manager.createTask(task);
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        String response;
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = manager.getTask(id);
            response = gson.toJson(task);
        } else {
            List<Task> tasks = manager.getAllTasks();
            response = gson.toJson(tasks);
        }
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.deleteTaskById(id);
        } else {
            manager.deleteAllTasks();
        }
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }
}