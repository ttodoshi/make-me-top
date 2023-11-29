package org.example.planet.exception.classes.planet;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(Long planetId) {
        super("Не удалось найти информацию о планете " + planetId + " в памяти бортового компьютера");
    }
}
