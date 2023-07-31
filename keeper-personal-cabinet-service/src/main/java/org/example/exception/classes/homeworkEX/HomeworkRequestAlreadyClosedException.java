package org.example.exception.classes.homeworkEX;

public class HomeworkRequestAlreadyClosedException extends RuntimeException {
    public HomeworkRequestAlreadyClosedException(Integer requestId) {
        super("Запрос " + requestId + " на проверку задания уже закрыт");
    }
}
