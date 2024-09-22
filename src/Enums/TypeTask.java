package Enums;

public enum TypeTask {
    TASK, SUBTASK, EPIC;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
