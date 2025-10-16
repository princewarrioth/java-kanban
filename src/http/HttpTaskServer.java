package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import http.handler.PrioritizedHandler;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager manager;

    public static final int PORT = 8080;

    public HttpTaskServer() throws IOException {
        manager = Managers.getDefault();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);


        server.createContext("/tasks", new http.handler.TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new http.handler.PrioritizedHandler(manager));
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Регистрируем обработчики
        server.createContext("/tasks", new http.handler.TaskHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        System.out.println("Сервер запущен на порту " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен.");
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }
}