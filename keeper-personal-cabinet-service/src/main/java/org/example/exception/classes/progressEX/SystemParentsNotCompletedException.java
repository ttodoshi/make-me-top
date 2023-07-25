package org.example.exception.classes.progressEX;

public class SystemParentsNotCompletedException extends RuntimeException {
    public SystemParentsNotCompletedException(Integer systemId) {
        super("У системы " + systemId + " есть неисследованные родители");
    }
}
