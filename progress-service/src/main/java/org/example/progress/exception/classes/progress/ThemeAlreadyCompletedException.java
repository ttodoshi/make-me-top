package org.example.progress.exception.classes.progress;

public class ThemeAlreadyCompletedException extends RuntimeException {
    public ThemeAlreadyCompletedException(Integer courseThemeId) {
        super("Тема " + courseThemeId + " уже полностью завершена");
    }
}
