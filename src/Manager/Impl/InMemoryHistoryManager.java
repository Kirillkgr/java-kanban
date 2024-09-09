package Manager.Impl;

import Models.Task;
import Manager.Node;
import Manager.HistoryManager;

import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    final HashMap<Integer, Node<Task>> mapOfHistory;
    private Node<Task> head;// Родительский объект
    private Node<Task> tail;// Дочерний объект

    public InMemoryHistoryManager() {
        this.mapOfHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        linkLastObject(task);
    }

    @Override
    public void remove(int id) {
        removeHistoryById(id);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return getAllTasksFromHistory();
    }

    private void linkLastObject(Task task) {
        if (mapOfHistory.containsKey(task.getId())) {
            remove(task.getId());
        } // Если существует удаляем
        Node<Task> newNode = new Node<>(tail, null, task);
        if (tail != null) {// Если существует дочерний, то указываем что следующем будит новый объект
            tail.setNext(newNode);
        }
        tail = newNode;// Иначе новый будит дочерним
        if (head == null) {
            head = tail;
        }
        mapOfHistory.put(task.getId(), newNode); // помещаем новый в мапу
    }

    private void removeHistoryById(int id) {
        Node<Task> nodeToRemove = mapOfHistory.remove(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
        }
    }

    private void removeNode(Node<Task> node) {
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
    }

    private LinkedList<Models.Task> getAllTasksFromHistory() {
        LinkedList<Models.Task> tasks = new LinkedList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.getElement());
            current = current.getNext();
        }
        return tasks;
    }
}