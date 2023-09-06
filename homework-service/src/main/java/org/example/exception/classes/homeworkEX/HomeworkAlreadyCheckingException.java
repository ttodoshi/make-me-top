package org.example.exception.classes.homeworkEX;

public class HomeworkAlreadyCheckingException extends RuntimeException {
    public HomeworkAlreadyCheckingException(Integer homeworkId) {
        super("Задание " + homeworkId + " сейчас уже проверяется");
    }
}
