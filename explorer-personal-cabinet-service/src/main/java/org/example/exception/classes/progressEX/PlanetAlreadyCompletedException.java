package org.example.exception.classes.progressEX;

public class PlanetAlreadyCompletedException extends RuntimeException {
    public PlanetAlreadyCompletedException() {
        super("Планета уже полностью завершена");
    }
}
