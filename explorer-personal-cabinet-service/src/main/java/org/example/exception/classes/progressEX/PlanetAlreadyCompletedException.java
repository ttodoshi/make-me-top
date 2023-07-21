package org.example.exception.classes.progressEX;

public class PlanetAlreadyCompletedException extends RuntimeException {
    public PlanetAlreadyCompletedException(Integer courseThemeId) {
        super("Тема " + courseThemeId + " уже полностью завершена");
    }
}
