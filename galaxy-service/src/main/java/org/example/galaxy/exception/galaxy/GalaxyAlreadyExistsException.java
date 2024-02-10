package org.example.galaxy.exception.galaxy;

public class GalaxyAlreadyExistsException extends RuntimeException {
    public GalaxyAlreadyExistsException(String name) {
        super("По информации бортового компьютера, галактика '" + name + "' уже существует");
    }
}
