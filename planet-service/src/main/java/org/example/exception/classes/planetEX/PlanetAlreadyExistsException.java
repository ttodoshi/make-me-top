package org.example.exception.classes.planetEX;

public class PlanetAlreadyExistsException extends RuntimeException {
    public PlanetAlreadyExistsException() {
        super("По информации бортового компьютера, данная планета уже существует");
    }
}
