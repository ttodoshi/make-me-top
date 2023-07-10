package org.example.exception.classes.galaxyEX;

public class GalaxyNotFoundException extends RuntimeException {
    public GalaxyNotFoundException() {
        super("Не удалось найти информацию о данной галактике в памяти бортового компьютера");
    }
}
