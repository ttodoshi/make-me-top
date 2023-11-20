package org.example.planet.exception.classes.galaxy;

public class GalaxyNotFoundException extends RuntimeException {
    public GalaxyNotFoundException(Integer galaxyId) {
        super("Не удалось найти информацию о галактике " + galaxyId + " в памяти бортового компьютера");
    }
}
