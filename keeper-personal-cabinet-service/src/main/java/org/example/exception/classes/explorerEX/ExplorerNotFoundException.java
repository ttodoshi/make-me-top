package org.example.exception.classes.explorerEX;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException() {
        super("Такой исследователь не найден");
    }
}
