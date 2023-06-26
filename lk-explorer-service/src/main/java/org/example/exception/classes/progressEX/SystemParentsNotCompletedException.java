package org.example.exception.classes.progressEX;

public class SystemParentsNotCompletedException extends RuntimeException {
    public SystemParentsNotCompletedException() {
        super("У системы есть неисследованные родители");
    }
}
