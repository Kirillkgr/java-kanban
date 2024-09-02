package Manager;

import Models.Task;

public class Node<T extends Task> {
    Node<T> prev;
    Node<T> next;
    T element;

    public Node(Node<T> prev, Node<T> next, T element) {
        this.prev = prev;
        this.next = next;
        this.element = element;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public T getElement() {
        return element;
    }
}
