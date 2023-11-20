package org.example.homework.exception.classes.homework;

public class HomeworkNotFoundException extends RuntimeException {
    public HomeworkNotFoundException(Integer homeworkId) {
        super("Задание " + homeworkId + " не найдено");
    }
}
