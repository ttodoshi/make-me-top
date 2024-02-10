package org.example.progress.exception.progress;

public class HomeworkNotCompletedException extends RuntimeException {
    public HomeworkNotCompletedException(Long themeId) {
        super("Завершены не все задания по теме " + themeId);
    }
}

