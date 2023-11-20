package org.example.courseregistration.exception.classes.progress;

public class AlreadyStudyingException extends RuntimeException {
    public AlreadyStudyingException() {
        super("Вас уже приняли на обучение на данном курсе");
    }
}

