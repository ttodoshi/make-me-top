package org.example.homework.exception.homework;

public class HomeworkIsStillEditingException extends RuntimeException {
    public HomeworkIsStillEditingException(Long homeworkId, Long explorerId) {
        super("Задание " + homeworkId + " всё ещё редактируется исследователем " + explorerId);
    }
}
