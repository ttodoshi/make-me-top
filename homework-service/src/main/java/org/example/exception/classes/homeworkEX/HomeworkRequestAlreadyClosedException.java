package org.example.exception.classes.homeworkEX;

public class HomeworkRequestAlreadyClosedException extends RuntimeException {
    public HomeworkRequestAlreadyClosedException(Integer requestId) {
        super("Запрос на проверку " + requestId + " уже был закрыт");
    }
}
