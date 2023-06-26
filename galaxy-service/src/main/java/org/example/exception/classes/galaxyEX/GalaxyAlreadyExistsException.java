package org.example.exception.classes.galaxyEX;

public class GalaxyAlreadyExistsException extends RuntimeException {
    public GalaxyAlreadyExistsException() {
        super("По информации бортового компьютера, данная галактика уже существует");
    }
}
