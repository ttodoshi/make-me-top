package org.example.progress.exception.classes.explorer;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }

    public ExplorerNotFoundException(Integer explorerId) {
        super("Исследователь " + explorerId + " не найден");
    }
}
