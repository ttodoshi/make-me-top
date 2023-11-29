package org.example.homework.exception.classes.homework;

public class HomeworkRequestAlreadyClosedException extends RuntimeException {
    public HomeworkRequestAlreadyClosedException(Long requestId) {
        super("Запрос на проверку " + requestId + " уже был закрыт");
    }
}
