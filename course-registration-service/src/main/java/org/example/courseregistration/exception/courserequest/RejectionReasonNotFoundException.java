package org.example.courseregistration.exception.courserequest;

public class RejectionReasonNotFoundException extends RuntimeException {
    public RejectionReasonNotFoundException() {
        super("Причина отказа не найдена");
    }
}
