package org.example.exception.classes.explorerEX;

public class ExplorerGroupNotFoundException extends RuntimeException {
    public ExplorerGroupNotFoundException(Integer groupId) {
        super("Группа " + groupId + " не найдена");
    }
}
