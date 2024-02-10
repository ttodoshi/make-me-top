package org.example.homework.exception.homework;

public class HomeworkRequestAlreadyClosedException extends RuntimeException {
    public HomeworkRequestAlreadyClosedException(Long requestId) {
        super("Запрос на проверку " + requestId + " уже был закрыт");
    }
}
