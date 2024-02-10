package org.example.courseregistration.exception.progress;

public class SystemParentsNotCompletedException extends RuntimeException {
    public SystemParentsNotCompletedException(Long systemId) {
        super("У системы " + systemId + " есть неисследованные родители");
    }
}
