package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        removeNode(task.getId());
        Node newNode = linkLast(task);
        nodeMap.put(task.getId(), newNode);
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
        Node currentNode = first;
        while (currentNode != null) {
            taskList.add(currentNode.task);
            currentNode = currentNode.next;
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
    public void addTask(Task task) {
        add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        add(epic);
    }

    @Override
    public void addSubtask(SubTask subtask) {
        add(subtask);
    }

    @Override
    public List<Task> getHistoryTasks() {
        return getTasks();
    }
}
