package Manager.Impl;

import Manager.HistoryManager;
import Manager.Node;
import Models.Task;

import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> historyTask;

    public InMemoryHistoryManager() {
        historyTask = new LinkedList<>();
    }

    public void add(Task task) {
        historyTask.push(task);
        if (historyTask.size() > 10) {
            historyTask.remove(historyTask.getFirst());
        }
    }

    @Override
    public void remove(int id) {

    }

    public LinkedList<Task> getHistory() {
        return historyTask;
    }

    static final class LinkedListOfHistory<T extends Task> {
        final HashMap<Integer, Node<T>> mapOfHistory;
        private Node<T> head; // Родительский объект
        private Node<T> last; // Дочерний объект

        public LinkedListOfHistory() {
            head = null;
            last = null;
            mapOfHistory = new HashMap<>();
        }

        private void linkLastObject(T object) {
            if (last == null) {
                Node<T> node = new Node<>(null, null, object);
                this.head = this.last = node;
                mapOfHistory.put(object.getId(), node);
            } else {
                Node<T> originalNode = this.last;
                if (mapOfHistory.containsKey(object.getId())) {
                    if (last.getElement().equals(object))
                        return;
                    // ToDO Дописать удаление объекта если он содержится в мапе
                }
                originalNode.setNext(last = new Node<>(originalNode, null, object));
                mapOfHistory.put(object.getId(), last);
            }
        }

    }
}
