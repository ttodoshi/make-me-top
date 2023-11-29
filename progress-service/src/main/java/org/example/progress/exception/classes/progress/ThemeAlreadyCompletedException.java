package org.example.progress.exception.classes.progress;

public class ThemeAlreadyCompletedException extends RuntimeException {
    public ThemeAlreadyCompletedException(Long courseThemeId) {
        super("Тема " + courseThemeId + " уже полностью завершена");
    }
}
