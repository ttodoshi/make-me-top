package org.example.courseregistration.exception.classes.progress;

public class SystemParentsNotCompletedException extends RuntimeException {
    public SystemParentsNotCompletedException(Integer systemId) {
        super("У системы " + systemId + " есть неисследованные родители");
    }
}
