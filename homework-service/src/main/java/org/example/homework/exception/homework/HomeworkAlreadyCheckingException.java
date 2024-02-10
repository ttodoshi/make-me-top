package org.example.homework.exception.homework;

public class HomeworkAlreadyCheckingException extends RuntimeException {
    public HomeworkAlreadyCheckingException(Long homeworkId) {
        super("Задание " + homeworkId + " сейчас уже проверяется");
    }
}
