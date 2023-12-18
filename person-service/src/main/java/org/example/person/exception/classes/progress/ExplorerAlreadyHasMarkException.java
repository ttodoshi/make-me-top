package org.example.person.exception.classes.progress;

public class ExplorerAlreadyHasMarkException extends RuntimeException {
    public ExplorerAlreadyHasMarkException() {
        super("Этот исследователь уже завершил своё обучение");
    }
}
