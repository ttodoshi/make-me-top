package org.example.person.exception.explorer;

public class ExplorerGroupNotFoundException extends RuntimeException {
    public ExplorerGroupNotFoundException() {
        super("Группа не найдена");
    }

    public ExplorerGroupNotFoundException(Long groupId) {
        super("Группа " + groupId + " не найдена");
    }
}
