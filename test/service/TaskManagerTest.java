package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.history_manager.HistoryManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task task1 = new Task("задача", "описание", Status.NEW, 300, LocalDateTime.of(2024, 4, 20, 12, 12));
    protected Task task2 = new Task("задача2", "описание2", Status.NEW, 200, LocalDateTime.of(2025, 4, 20, 13, 12));
    protected Epic epic1 = new Epic("Кино", "идем в кино в воскресенье");
    protected Epic epic2 = new Epic("Поездка", "Собраться");
    protected SubTask subTask1 = new SubTask("кино", "выбрать кино", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
    protected SubTask subTask2 = new SubTask("билеты", "купить билеты", Status.NEW, 12, LocalDateTime.of(2024, 10, 20, 12, 12), epic1.getId());
    protected SubTask subTask3 = new SubTask("Собраться", "собрать чемодан", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 50), epic1.getId());
    protected HistoryManager historyManager;

    @Test
    void testUpdateEpicStatusEmptySubTasks() {
        Epic epic = new Epic("Test Epic", "Test Description");
        epic.setId(1);
        manager.createEpic(epic);
        manager.updateEpic(epic);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testUpdateEpicStatusAllSubTasksNew() {
        Epic epic = new Epic("Test Epic", "Test Description");
        epic.setId(1);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.NEW, 12, LocalDateTime.of(2024, 10, 20, 12, 12), epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testUpdateEpicStatusAllSubTasksDone() {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.DONE, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.DONE, 12, LocalDateTime.of(2024, 10, 20, 12, 12), epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void testUpdateEpicStatusSubTasksNewAndDone() {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.NEW, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.DONE, 12, LocalDateTime.of(2024, 10, 20, 12, 12), epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testUpdateEpicStatusSubTasksInProgress() {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.DONE, 12, LocalDateTime.of(2024, 10, 20, 12, 12), epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void createTaskStandard() {
        int id = manager.createTask(task1);
        assertEquals(id, task1.getId());

        Task retrievedTask = manager.getTask(id);
        assertNotNull(retrievedTask, "Задача не найдена.");
        assertEquals(task1, retrievedTask, "Задачи не совпадают.");

        List<Task> tasks = (List<Task>) manager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Некорректный id задачи");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createTaskEmptyTaskList() {
        manager.deleteAllTasks();
        int id = manager.createTask(task1);
        assertEquals(id, task1.getId());

        Task retrievedTask = manager.getTask(id);
        assertNotNull(retrievedTask, "Задача не найдена.");
        assertEquals(task1, retrievedTask, "Задачи не совпадают.");

        List<Task> tasks = (List<Task>) manager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Некорректный id задачи");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createTaskWithoutId() {
        int taskId = manager.createTask(task1);
        assertTrue(taskId > 0);
    }

    @Test
    public void createEpicStandard() {
        int id = manager.createEpic(epic1);
        assertEquals(id, epic1.getId());

        Epic retrievedEpic = manager.getEpic(id);
        assertNotNull(retrievedEpic, "Эпик не найден.");
        assertEquals(epic1, retrievedEpic, "Эпики не совпадают.");
        assertEquals(Status.NEW, retrievedEpic.getStatus(), "Статус не соответствует ожидаемому.");

        List<Epic> epics = (List<Epic>) manager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Некорректный id эпика.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void createEpicEmptyEpicList() {
        manager.deleteAllEpic();
        int id = manager.createEpic(epic1);
        assertEquals(id, epic1.getId());

        Epic retrievedEpic = manager.getEpic(id);
        assertNotNull(retrievedEpic, "Эпик не найден.");
        assertEquals(epic1, retrievedEpic, "Эпики не совпадают.");
        assertEquals(Status.NEW, retrievedEpic.getStatus(), "Статус не соответствует ожидаемому.");

        List<Epic> epics = (List<Epic>) manager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Некорректный id эпика.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    public void createEpicWithoutId() {
        int taskId = manager.createEpic(epic1);
        assertTrue(taskId > 0);
    }

    @Test
    public void createSubTaskStandard() {
        int epicId = manager.createEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        int subTaskId = manager.createSubTask(subTask1);

        assertEquals(subTaskId, subTask1.getId(), "Некорректный ID подзадачи");

        SubTask retrievedSubTask = manager.getSubTask(subTaskId);
        assertNotNull(retrievedSubTask, "Подзадача не найдена");
        assertEquals(subTask1, retrievedSubTask, "Подзадачи не совпадают");

        List<SubTask> subTasks = (List<SubTask>) manager.getSubTasksList();
        assertNotNull(subTasks, "Подзадачи не возвращаются");
        assertEquals(1, subTasks.size(), "Некорректное количество подзадач");
        assertEquals(subTask1, subTasks.get(0), "Подзадачи не совпадают");

        Epic retrievedEpic = manager.getEpic(epicId);
        assertEquals(1, retrievedEpic.getSubTaskIdList().size(), "Некорректное количество подзадач в эпике");
        assertEquals(subTaskId, (int) retrievedEpic.getSubTaskIdList().get(0), "Некорректный ID подзадачи в списке эпика");
    }

    @Test
    public void createSubTaskDuplicateTime() {
        manager.createEpic(epic1);

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 200, LocalDateTime.of(2023, 10, 20, 12, 13), epic1.getId());
        assertThrows(IllegalArgumentException.class, () -> manager.createSubTask(subTask2), "Время задач пересекается.");
    }

    @Test
    public void createSubTaskInvalidEpicId() {
        SubTask subTask = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), -1);

        assertThrows(IllegalArgumentException.class, () -> manager.createSubTask(subTask), "Epic с заданным идентификатором не существует");
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        manager.createTask(task1);

        assertThrows(NullPointerException.class, () -> manager.updateTask(null));
        assertEquals(task1, manager.getTask(task1.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        manager.createEpic(epic1);

        assertThrows(NullPointerException.class, () -> manager.updateEpic(null));
        assertEquals(epic1, manager.getEpic(epic1.getId()));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNull() {
        manager.createEpic(epic1);
        SubTask subTask = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        manager.createSubTask(subTask);

        assertThrows(NullPointerException.class, () -> manager.updateSubTask(null));
        assertEquals(subTask, manager.getSubTask(subTask.getId()));
    }

    @Test
    public void shouldDeleteAllTasks() {
        manager.createTask(task1);
        manager.deleteAllTasks();

        assertTrue(manager.getTaskList().isEmpty());
    }

    @Test
    public void shouldDeleteAllEpics() {
        manager.createEpic(epic1);
        manager.deleteAllEpic();

        assertTrue(manager.getSubTasksList().isEmpty());
        assertTrue(manager.getEpicList().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubTasks() {
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2024, 10, 20, 12, 12), epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.deleteAllSubTasks();

        assertTrue(epic1.getSubTaskIdList().isEmpty());
        assertTrue(manager.getSubTasksList().isEmpty());
    }

    @Test
    public void shouldDeleteAllTasksFromEmptyList() {
        manager.deleteAllTasks();
        assertTrue(manager.getTaskList().isEmpty());
    }

    @Test
    public void shouldDeleteAllEpicsFromEmptyList() {
        manager.deleteAllEpic();

        assertTrue(manager.getSubTasksList().isEmpty());
        assertTrue(manager.getEpicList().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubTasksFromEmptyList() {
        manager.deleteAllSubTasks();

        assertTrue(manager.getSubTasksList().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        manager.createTask(task1);
        int taskId = task1.getId();
        manager.deleteTask(taskId);

        assertThrows(IllegalArgumentException.class, () -> manager.getTask(taskId), "Task с таким id не существует.");
    }

    @Test
    public void shouldDeleteEpicById() {
        manager.createEpic(epic1);
        int epicId = epic1.getId();
        manager.deleteEpic(epicId);

        assertThrows(IllegalArgumentException.class, () -> manager.getEpic(epicId), "Epic с таким id не существует.");
        assertTrue(manager.getSubTasks(epic1).isEmpty());
    }

    @Test
    public void shouldDeleteSubTaskById() {
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        manager.createSubTask(subTask1);
        manager.deleteSubTask(subTask1.getId());
        int subTaskId = subTask1.getId();

        assertTrue(epic1.getSubTaskIdList().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> manager.getSubTask(subTaskId), "SubTask с таким id не существует.");
    }

    @Test
    public void shouldNotDeleteNonExistentTask() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.deleteTask(-1);
        });
    }

    @Test
    public void shouldNotDeleteNonExistentEpic() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.deleteEpic(-1);
        });
    }

    @Test
    public void shouldNotDeleteNonExistentSubTask() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.deleteSubTask(-1);
        });
    }

    @Test
    public void shouldGetTaskById() {
        manager.createTask(task1);
        int taskId = task1.getId();

        assertEquals(task1, manager.getTask(taskId));
    }

    @Test
    public void shouldGetEpicById() {
        manager.createEpic(epic1);
        int epicId = epic1.getId();

        assertEquals(epic1, manager.getEpic(epicId));
    }

    @Test
    public void shouldGetSubTaskById() {
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        manager.createSubTask(subTask1);
        int subTaskId = subTask1.getId();

        assertEquals(subTask1, manager.getSubTask(subTaskId));
    }

    @Test
    public void shouldNotGetNonExistentTask() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getTask(-1);
        });
    }

    @Test
    public void shouldNotGetNonExistentEpic() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getEpic(-1);
        });
    }

    @Test
    public void shouldNotGetNonExistentSubTask() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getSubTask(-1);
        });
    }

    @Test
    public void testGetPrioritizedTasksSort() {
        manager.createEpic(epic1);
        manager.createTask(task1);
        manager.createTask(task2);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 60, LocalDateTime.of(2023, 10, 20, 12, 0), epic1.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.IN_PROGRESS, 60, LocalDateTime.of(2023, 10, 20, 14, 10), epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        ArrayList<Task> sortedTasks = manager.getPrioritizedTasks();

        Iterator<Task> iterator = sortedTasks.iterator();
        assertEquals(subTask1, iterator.next());
        assertEquals(subTask2, iterator.next());
        assertEquals(task1, iterator.next());
        assertEquals(task2, iterator.next());
    }

    @Test
    public void testGetPrioritizedTasksWithEmptyHashMap() {
        ArrayList<Task> sortedTasks = manager.getPrioritizedTasks();

        assertEquals(0, sortedTasks.size());
    }
    @Test
    public void shouldReturnAllTasks() {
        manager.createTask(task1);
        manager.createTask(task2);

        Collection<Task> tasks = manager.getTaskList();

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
    public void shouldReturnAllEpics() {
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Collection<Epic> epics = manager.getEpicList();

        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    public void shouldReturnAllSubTasks() {
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 300, LocalDateTime.of(2023, 10, 20, 12, 12), epic1.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.IN_PROGRESS, 400, LocalDateTime.of(2024, 10, 20, 12, 13), epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Collection<SubTask> subTasks = manager.getSubTasksList();

        assertEquals(2, subTasks.size());
        assertTrue(subTasks.contains(subTask1));
        assertTrue(subTasks.contains(subTask2));
    }

    @Test
    public void shouldReturnEmptyTaskList() {
        Collection<Task> taskList = manager.getTaskList();

        assertTrue(taskList.isEmpty());
    }

    @Test
    public void shouldReturnEmptyEpicList() {
        Collection<Epic> epicList = manager.getEpicList();

        assertTrue(epicList.isEmpty());
    }

    @Test
    public void shouldReturnEmptySubTaskList() {
        Collection<SubTask> subTaskList = manager.getSubTasksList();

        assertTrue(subTaskList.isEmpty());
    }

    @Test
    public void shouldUpdateEpicDurationAndStartTimes() {
        manager.createEpic(epic1);
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask Description", Status.IN_PROGRESS, 60, LocalDateTime.of(2023, 10, 20, 12, 0), epic1.getId());
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask Description", Status.IN_PROGRESS, 60, LocalDateTime.of(2023, 10, 20, 13, 10), epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(130, epic1.getDuration());
        assertEquals(LocalDateTime.of(2023, 10, 20, 12, 0), epic1.getStartTime());
        assertEquals(LocalDateTime.of(2023, 10, 20, 14, 10), epic1.getEndTime());
    }

    @Test
    public void testUpdateDurationAndStartTimesWithNoSubtasks() {
        manager.createEpic(epic1);

        assertNull(epic1.getStartTime());
        assertNull(epic1.getEndTime());
        assertEquals(0, epic1.getDuration());
    }

    @Test
    public void checkTaskOverlapWithDuplicateTime() {
        Task task1 = new Task("задача", "описание", Status.NEW, 300, LocalDateTime.of(2024, 4, 20, 12, 12));
        Task task2 = new Task("задача2", "описание2", Status.NEW, 300, LocalDateTime.of(2024, 4, 20, 13, 12));
        manager.createTask(task1);

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2), "Время задач пересекается.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskManagerTest<?> that = (TaskManagerTest<?>) o;
        return Objects.equals(manager, that.manager) && Objects.equals(task1, that.task1) && Objects.equals(task2, that.task2) && Objects.equals(epic1, that.epic1) && Objects.equals(epic2, that.epic2) && Objects.equals(subTask1, that.subTask1) && Objects.equals(subTask2, that.subTask2) && Objects.equals(subTask3, that.subTask3) && Objects.equals(historyManager, that.historyManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, task1, task2, epic1, epic2, subTask1, subTask2, subTask3, historyManager);
    }
}