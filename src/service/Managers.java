package service;

import service.HTTP_Manager.HTTPTaskManager;
import service.history_manager.HistoryManager;
import service.history_manager.InMemoryHistoryManager;


public class Managers {

    public static TaskManager getDefault() {
        return new HTTPTaskManager(false);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
