package org.example.homework.exception.homework;

public class HomeworkRequestNotFound extends RuntimeException {
    public HomeworkRequestNotFound(Long homeworkId, Long explorerId) {
        super("Система не смогла найти информацию о запросе на проверку задания " + homeworkId + " от исследователя " + explorerId);
    }

    public HomeworkRequestNotFound(Long homeworkRequestId) {
        super("Система не смогла найти информацию о запросе " + homeworkRequestId + " на проверку задания");
    }
}
