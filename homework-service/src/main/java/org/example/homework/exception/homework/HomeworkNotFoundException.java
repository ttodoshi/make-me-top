package org.example.homework.exception.homework;

public class HomeworkNotFoundException extends RuntimeException {
    public HomeworkNotFoundException(Long homeworkId) {
        super("Задание " + homeworkId + " не найдено");
    }
}
