package Manager.Impl;

import Manager.HistoryManager;
import Manager.Node;
import Models.Task;

import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedListOfHistory<Task> historyTask;

    public InMemoryHistoryManager() {
        historyTask = new LinkedListOfHistory<>();
    }

    @Override
    public void add(Task task) {
        historyTask.linkLastObject(task);
    }

    @Override
    public void remove(int id) {
        historyTask.remove(id);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return historyTask.getTasks();
    }

    static final class LinkedListOfHistory<T extends Task> {
        final HashMap<Integer, Node<T>> mapOfHistory;
        private Node<T> head;// Родительский объект
        private Node<T> next;// Дочерний объект

        public LinkedListOfHistory() {
            mapOfHistory = new HashMap<>();
            head = null;
            next = null;
        }

        public void linkLastObject(T task) {
            if (mapOfHistory.containsKey(task.getId())) {
                remove(task.getId());
            } // Если существует удаляем
            Node<T> newNode = new Node<>(next, null, task);
            if (next != null) {// Если существует дочерний, то указываем что следующем будит новый объект
                next.setNext(newNode);
            }
                next = newNode;// Иначе новый будит дочерним
            if (head == null) {
                head = next;
            }
            mapOfHistory.put(task.getId(), newNode); // помещаем новый в мапу
        }

        public void remove(int id) {
            Node<T> nodeToRemove = mapOfHistory.remove(id);
            if (nodeToRemove != null) {
                removeNode(nodeToRemove);
            }
        }

        private void removeNode(Node<T> node) {
            if (node.getPrev() != null) {
                node.getPrev().setNext(node.getNext());
            } else {
                head = node.getNext();
            }
            if (node.getNext() != null) {
                node.getNext().setPrev(node.getPrev());
            } else {
                next = node.getPrev();
            }
        }

        public LinkedList<Task> getTasks() {
            LinkedList<Task> tasks = new LinkedList<>();
            Node<T> current = head;
            while (current != null) {
                tasks.add(current.getElement());
                current = current.getNext();
            }
            return tasks;
        }
    }
}
