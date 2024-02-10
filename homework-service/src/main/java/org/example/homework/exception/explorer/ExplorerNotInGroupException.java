package org.example.homework.exception.explorer;

public class ExplorerNotInGroupException extends RuntimeException {
    public ExplorerNotInGroupException() {
        super("Вы не относитесь к этой группе");
    }
}
