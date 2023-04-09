package service.historymanager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Такой задачи нет.");
            return;
        }
        removeNode(task.getId());
        nodeMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private void removeNode(Integer id) {
        Node node = nodeMap.remove(id);
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;

            if (node.next == null) {
                last = node.prev;
            } else {
                node.next.prev = node.prev;
            }
        } else {
            first = node.next;
            if (first == null) {
                last = null;
            } else {
                first.prev = null;
            }
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        Node node = first;
        while (node != null) {
            taskList.add(node.task);
            node = node.next;
        }
        return taskList;
    }

    private Node linkLast(Task task) {
        Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        return node;
    }

    @Override
    public List<Task> getHistoryTasks() {
        return getTasks();
    }
}
