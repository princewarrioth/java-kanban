import manager.FileBackedTaskManager;
import model.*;
import status.Status;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;
    private File testFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("test.csv").toFile();
        manager = new FileBackedTaskManager(testFile);
    }

    @Test
    void testEpicStatusCalculationAfterLoad() {
        FileBackedTaskManager manager1 = new FileBackedTaskManager(testFile);
        Epic epic = new Epic("Epic", "Description");
        manager1.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc1", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Sub2", "Desc2", Status.NEW, epic.getId());
        manager1.createSubtask(subtask1);
        manager1.createSubtask(subtask2);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(testFile);
        Epic loadedEpic = manager2.getEpic(epic.getId());
        assertEquals(Status.IN_PROGRESS, loadedEpic.getStatus());
    }
}