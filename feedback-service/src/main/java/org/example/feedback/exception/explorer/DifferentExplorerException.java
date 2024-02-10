package org.example.feedback.exception.explorer;

public class DifferentExplorerException extends RuntimeException {
    public DifferentExplorerException() {
        super("У вас нет прав, чтобы сделать это");
    }
}
