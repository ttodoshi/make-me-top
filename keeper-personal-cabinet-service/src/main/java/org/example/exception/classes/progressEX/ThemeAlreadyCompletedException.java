package org.example.exception.classes.progressEX;

public class ThemeAlreadyCompletedException extends RuntimeException {
    public ThemeAlreadyCompletedException(Integer courseThemeId) {
        super("Тема " + courseThemeId + " уже полностью завершена");
    }
}
