package org.example.homework.exception.person;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException() {
        super("Система не смогла найти у себя в памяти данные об этом человеке");
    }
}
