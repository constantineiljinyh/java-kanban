package service.inmemory_taskmanager;

import model.Epic;
import org.junit.jupiter.api.BeforeEach;
import service.Managers;
import service.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest <InMemoryTaskManager>{

    @BeforeEach
    protected void createTasks() {
        manager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }

    @BeforeEach
    protected void createEpics() {
        manager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }

    protected void createSubtasks(Epic epic) {
        manager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
    }
}


