package org.example.exception.classes.galaxyEX;

public class GalaxyAlreadyExistsException extends RuntimeException {
    public GalaxyAlreadyExistsException(String name) {
        super("По информации бортового компьютера, галактика '" + name + "' уже существует");
    }
}
