package org.example.homework.exception.classes.explorer;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException(Integer explorerId) {
        super("Исследователь " + explorerId + " не найден");
    }

    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }
}
