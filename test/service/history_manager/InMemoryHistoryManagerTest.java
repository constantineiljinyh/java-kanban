package service.history_manager;

import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest extends InMemoryHistoryManager {
    protected HistoryManager historyManager;
    protected Task task1 ;
    protected Task task2 ;
    protected Epic epic1;
    protected Epic epic2 ;
    @BeforeEach
    protected void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("задача", "описание", Status.NEW, 1);
        task2 = new Task("задача2", "описание2", Status.NEW, 2);
        epic1 = new Epic("Кино", "идем в кино в воскресенье", 3);
        epic2 = new Epic("Поездка", "Собраться", 4);
    }

    @Test
    void testRemoveNodeFromEmptyHistory() {
        historyManager.remove(1);

        assertTrue(historyManager.getHistoryTasks().isEmpty());
    }

    @Test
    void testRemoveNodeWithDuplicateIds() {
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistoryTasks().size());
    }

    @Test
    void testRemoveFirstNode() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.remove(1);

        assertEquals(task2, historyManager.getHistoryTasks().get(0));
        assertEquals(epic1, historyManager.getHistoryTasks().get(1));
    }

    @Test
    void testRemoveLastNode() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.remove(3);

        assertEquals(task1, historyManager.getHistoryTasks().get(0));
        assertEquals(task2, historyManager.getHistoryTasks().get(1));
    }

    @Test
    void testRemoveMiddleNode() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.remove(2);

        assertEquals(task1, historyManager.getHistoryTasks().get(0));
        assertEquals(epic1, historyManager.getHistoryTasks().get(1));
    }

    @Test
    void addNull() {
        historyManager.add(null);

        assertTrue(historyManager.getHistoryTasks().isEmpty());
    }
}