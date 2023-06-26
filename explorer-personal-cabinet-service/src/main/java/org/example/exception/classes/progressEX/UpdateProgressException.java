package org.example.exception.classes.progressEX;

public class UpdateProgressException extends RuntimeException {
    public UpdateProgressException() {
        super("Система не смогла обновить ваш прогресс из-за внутреннего сбоя");
    }
}
