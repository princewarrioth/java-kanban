package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends http.handler.BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new Gson();

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGet(exchange);
        } else {
            sendText(exchange, "{\"error\":\"Unsupported method\"}", 405);
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        List<Task> prioritized = manager.getPrioritizedTasks();
        sendText(h, gson.toJson(prioritized), 200);
    }
}