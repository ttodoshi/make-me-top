package org.example.homework.exception.classes.planet;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(Integer planetId) {
        super("Не удалось найти информацию о планете " + planetId + " в памяти бортового компьютера");
    }
}
