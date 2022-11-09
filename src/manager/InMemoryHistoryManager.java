package manager;

import java.util.ArrayList;
import java.util.List;

import task.Task;

import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    private void linkList(Task task) {
        Node newNode = new Node();
        newNode.setTask(task);
        removeNode(task.getId());
        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.setPrev(null);
            newNode.setNext(null);
        } else {
            newNode.setPrev(tail);
            newNode.setNext(null);
            tail.setNext(newNode);
            tail = newNode;
        }
        history.put(newNode.task.getId(), newNode);
    }

    private void removeNode(int id) { //в качестве аргумента получает узел и вырезает его
        Node node = history.remove(id);
        if (node != null) {
            Node prev = node.getPrev();
            Node next = node.getNext();
            if (head == node) { // prev = null
                head = node.getNext(); // головой становится след. элемент
            }
            if (tail == node) { // next == null
                tail = node.getPrev(); // хвостом становится предыдущий элемент
            }
            if (prev != null) {
                prev.setNext(next); // у пред.элемента перезаписывается след. эл
            }
            if (next != null) {
                next.setPrev(prev); // у след. элемента перезаписывается предыдущий
            }
        }
    }

    private List<Task> getTask() {
        List<Task> historyList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            historyList.add(node.getTask());
            node = node.getNext();
        }
        return historyList;
    }

    private Node getNode(int id) {
        return history.get(id);
    }

    @Override
    public void addTaskToHistory(Task task) {
        linkList(task);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTask();
    }


    class Node {
        private Task task;
        private Node prev;
        private Node next;

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
