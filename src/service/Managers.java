package service;

import service.history_manager.HistoryManager;
import service.history_manager.InMemoryHistoryManager;
import service.inmemory_taskmanager.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
