package org.example.exception.classes.explorerEX;

public class ExplorerNotFoundException extends RuntimeException {
    public ExplorerNotFoundException() {
        super("Кажется, вы не записывались на данный курс");
    }
}
