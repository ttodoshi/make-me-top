package org.example.exception.classes.explorerEX;

public class ExplorerNotInGroupException extends RuntimeException {
    public ExplorerNotInGroupException() {
        super("Вы не относитесь к этой группе");
    }
}
