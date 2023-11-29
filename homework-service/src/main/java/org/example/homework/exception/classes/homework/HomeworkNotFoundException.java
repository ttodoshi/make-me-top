package org.example.homework.exception.classes.homework;

public class HomeworkNotFoundException extends RuntimeException {
    public HomeworkNotFoundException(Long homeworkId) {
        super("Задание " + homeworkId + " не найдено");
    }
}
