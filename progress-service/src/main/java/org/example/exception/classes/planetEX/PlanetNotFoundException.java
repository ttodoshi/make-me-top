package org.example.exception.classes.planetEX;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(Integer planetId) {
        super("Не удалось найти информацию о планете " + planetId + " в памяти бортового компьютера");
    }
}
