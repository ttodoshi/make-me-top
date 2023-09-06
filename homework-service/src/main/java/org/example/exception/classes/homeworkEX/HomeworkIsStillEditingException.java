package org.example.exception.classes.homeworkEX;

public class HomeworkIsStillEditingException extends RuntimeException {
    public HomeworkIsStillEditingException(Integer homeworkId, Integer explorerId) {
        super("Задание " + homeworkId + " всё ещё редактируется исследователем " + explorerId);
    }
}
