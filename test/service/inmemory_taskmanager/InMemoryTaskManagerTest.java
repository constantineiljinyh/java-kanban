package service.inmemory_taskmanager;

import org.junit.jupiter.api.BeforeEach;
import service.TaskManagerTest;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }
}
