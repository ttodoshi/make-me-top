package org.example.exception.classes.explorerEX;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException(Integer courseId) {
        super("Кажется, вы не записывались на курс " + courseId);
    }

    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }
}
