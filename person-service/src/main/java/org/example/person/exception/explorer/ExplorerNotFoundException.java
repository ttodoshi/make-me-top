package org.example.person.exception.explorer;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }

    public ExplorerNotFoundException(Long explorerId) {
        super("Исследователь " + explorerId + " не найден");
    }
}
