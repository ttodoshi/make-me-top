package org.example.exception.classes.homeworkEX;

public class MarkAlreadyExists extends RuntimeException {
    public MarkAlreadyExists() {
        super("Оценка уже существует");
    }
}
