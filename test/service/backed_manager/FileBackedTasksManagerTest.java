package service.backed_manager;

import custom_exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManagerTest;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    File testFile;

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager();
        this.testFile = new File("resources/history.csv");
    }

    @Test
    public void testLoadFromFile() throws ManagerSaveException {
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epic1.getId());

        FileBackedTasksManager tasksManager = FileBackedTasksManager.loadFromFile(testFile);

        assertEquals(2, tasksManager.getTaskList().size());
        assertEquals(1, tasksManager.getEpicList().size());
        assertEquals(3, tasksManager.getHistoryTasks().size());

        assertEquals(task1, tasksManager.getTask(task1.getId()));
        assertEquals(task2, tasksManager.getTask(task2.getId()));
        assertEquals(epic1, tasksManager.getEpic(epic1.getId()));
    }

    @Test
    public void testLoadFromFileEmptyFile() throws IOException, ManagerSaveException {
        File tempFile = File.createTempFile("empty", ".csv");
        tempFile.deleteOnExit();
        manager.save();
        FileBackedTasksManager tasksManager = FileBackedTasksManager.loadFromFile(tempFile);

        assertTrue(tasksManager.getTaskList().isEmpty());
        assertTrue(tasksManager.getEpicList().isEmpty());
        assertTrue(tasksManager.getSubTasksList().isEmpty());
        assertTrue(tasksManager.getHistoryTasks().isEmpty());
    }

    @Test
    public void testLoadEpicsFromFile() throws ManagerSaveException {
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.getEpicList();
        manager.save();

        FileBackedTasksManager tasksManager = FileBackedTasksManager.loadFromFile(testFile);

        assertEquals(2, tasksManager.getEpicList().size());
        assertEquals(epic1, tasksManager.getEpic(epic1.getId()));
        assertEquals(epic2, tasksManager.getEpic(epic2.getId()));
    }
}