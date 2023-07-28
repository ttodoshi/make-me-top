package org.example.exception.classes.homeworkEX;

public class HomeworkNotFoundException extends RuntimeException {
    public HomeworkNotFoundException(Integer homeworkId) {
        super("Задание " + homeworkId + " не найдено");
    }
}
