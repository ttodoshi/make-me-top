package org.example.exception.classes.galaxyEX;

public class GalaxyNotFoundException extends RuntimeException {
    public GalaxyNotFoundException(Integer galaxyId) {
        super("Не удалось найти информацию о галактике " + galaxyId + " в памяти бортового компьютера");
    }
}
