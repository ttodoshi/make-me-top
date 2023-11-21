package org.example.planet.exception.classes.planet;

public class PlanetAlreadyExistsException extends RuntimeException {
    public PlanetAlreadyExistsException(String name) {
        super("По информации бортового компьютера, планета '" + name + "' уже существует");
    }
}
