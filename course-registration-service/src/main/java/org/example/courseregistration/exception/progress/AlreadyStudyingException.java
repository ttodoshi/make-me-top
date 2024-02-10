package org.example.courseregistration.exception.progress;

public class AlreadyStudyingException extends RuntimeException {
    public AlreadyStudyingException() {
        super("Вас уже приняли на обучение на данном курсе");
    }
}

