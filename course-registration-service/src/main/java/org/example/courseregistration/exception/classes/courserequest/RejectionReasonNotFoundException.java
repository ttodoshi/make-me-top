package org.example.courseregistration.exception.classes.courserequest;

public class RejectionReasonNotFoundException extends RuntimeException {
    public RejectionReasonNotFoundException() {
        super("Причина отказа не найдена");
    }
}
