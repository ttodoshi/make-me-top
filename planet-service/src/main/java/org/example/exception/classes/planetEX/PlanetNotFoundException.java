package org.example.exception.classes.planetEX;

public class PlanetNotFoundException extends RuntimeException {

    public PlanetNotFoundException() {
        super("Не удалось найти информацию о данной планете в памяти бортового компьютера");
    }
}
