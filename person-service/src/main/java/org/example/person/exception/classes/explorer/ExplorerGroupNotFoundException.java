package org.example.person.exception.classes.explorer;

public class ExplorerGroupNotFoundException extends RuntimeException {
    public ExplorerGroupNotFoundException(Integer groupId) {
        super("Группа " + groupId + " не найдена");
    }
}
