package service;

import service.backed_manager.FileBackedTasksManager;
import service.history_manager.HistoryManager;
import service.history_manager.InMemoryHistoryManager;


public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
