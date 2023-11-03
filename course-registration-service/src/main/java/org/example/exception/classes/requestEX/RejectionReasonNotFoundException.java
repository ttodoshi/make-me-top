package org.example.exception.classes.requestEX;

public class RejectionReasonNotFoundException extends RuntimeException {
    public RejectionReasonNotFoundException() {
        super("Причина отказа не найдена");
    }
}
