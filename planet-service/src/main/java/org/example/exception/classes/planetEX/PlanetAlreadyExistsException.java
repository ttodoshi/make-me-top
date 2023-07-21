package org.example.exception.classes.planetEX;

public class PlanetAlreadyExistsException extends RuntimeException {
    public PlanetAlreadyExistsException(String name) {
        super("По информации бортового компьютера, планета '" + name + "' уже существует");
    }
}
