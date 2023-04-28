package service.inmemory_taskmanager;

import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager>{
// тут нет уникальных тестов, просто нужен, что бы протестировать InMemoryTaskManager
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }
}


