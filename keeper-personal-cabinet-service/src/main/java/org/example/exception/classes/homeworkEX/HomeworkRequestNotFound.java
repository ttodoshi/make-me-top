package org.example.exception.classes.homeworkEX;

public class HomeworkRequestNotFound extends RuntimeException {
    public HomeworkRequestNotFound(Integer homeworkId, Integer explorerId) {
        super("Система не смогла найти информацию о запросе на проверку задания " + homeworkId + " от исследователя " + explorerId);
    }

    public HomeworkRequestNotFound(Integer homeworkRequestId) {
        super("Система не смогла найти информацию о запросе " + homeworkRequestId + " на проверку задания");
    }
}
