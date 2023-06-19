package org.example.exception.classes.planetEX;

public class PlanetNotFoundException extends RuntimeException {

    public PlanetNotFoundException() {
        super("Планета не найдена");
    }
}
