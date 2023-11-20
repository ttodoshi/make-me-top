package org.example.feedback.exception.classes.explorer;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }

    public ExplorerNotFoundException(Integer explorerId) {
        super("Исследователь " + explorerId + " не найден");
    }
}
