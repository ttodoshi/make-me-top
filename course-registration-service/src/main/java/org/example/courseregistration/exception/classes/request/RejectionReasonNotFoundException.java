package org.example.courseregistration.exception.classes.request;

public class RejectionReasonNotFoundException extends RuntimeException {
    public RejectionReasonNotFoundException() {
        super("Причина отказа не найдена");
    }
}
