package org.example.course.exception.classes.explorer;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException(Long explorerId) {
        super("Исследователь " + explorerId + " не найден");
    }

    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }
}
