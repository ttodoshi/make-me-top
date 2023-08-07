package org.example.exception.classes.homeworkEX;

public class HomeworkNotCompletedException extends RuntimeException {
    public HomeworkNotCompletedException(Integer themeId) {
        super("Завершены не все задания по теме " + themeId);
    }
}
