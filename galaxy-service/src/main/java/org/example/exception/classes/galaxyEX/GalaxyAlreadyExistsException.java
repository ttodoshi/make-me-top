package org.example.exception.classes.galaxyEX;

public class GalaxyAlreadyExistsException extends RuntimeException {
    public GalaxyAlreadyExistsException() {
        super("Галактика уже существует");
    }
}
