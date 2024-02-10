package org.example.person.exception.progress;

public class ExplorerAlreadyHasMarkException extends RuntimeException {
    public ExplorerAlreadyHasMarkException() {
        super("Этот исследователь уже завершил своё обучение");
    }
}
