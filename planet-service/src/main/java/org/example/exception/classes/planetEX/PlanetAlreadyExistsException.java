package org.example.exception.classes.planetEX;

public class PlanetAlreadyExistsException extends RuntimeException {
    public PlanetAlreadyExistsException() {
        super("Планета уже существует");
    }
}
