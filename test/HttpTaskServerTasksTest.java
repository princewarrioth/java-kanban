import com.google.gson.*;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Task;
import status.Status;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;

    private Gson gson = new GsonBuilder()
            // Duration
            .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toMillis()))
            .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, typeOfT, context) ->
                    Duration.ofMillis(json.getAsLong()))
            // LocalDateTime
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager(null);
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.now());

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Задача должна создаваться успешно");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Test 2", tasksFromManager.get(0).getName());
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test GET", "Check GET method", Status.NEW);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getName(), returnedTask.getName());
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Delete me", "Task for deletion", Status.NEW);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    void testDeleteAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Desc 1", Status.NEW);
        Task task2 = new Task("Task 2", "Desc 2", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertTrue(manager.getAllTasks().isEmpty());
    }
}