package org.example.homework.exception.classes.explorer;

public class ExplorerGroupNotFoundException extends RuntimeException {
    public ExplorerGroupNotFoundException(Long groupId) {
        super("Группа " + groupId + " не найдена");
    }
}
