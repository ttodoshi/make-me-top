package org.example.exception.classes.progressEX;

public class AlreadyStudyingException extends RuntimeException {
    public AlreadyStudyingException() {
        super("Вас уже приняли на обучение на данном курсе");
    }
}
