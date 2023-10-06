package org.example.exception.classes.explorerEX;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException(Integer explorerId) {
        super("Исследователь " + explorerId + " не найден");
    }

    public ExplorerNotFoundException() {
        super("Исследователь не найден");
    }
}
