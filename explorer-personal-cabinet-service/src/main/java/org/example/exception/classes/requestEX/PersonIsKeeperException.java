package org.example.exception.classes.requestEX;

public class PersonIsKeeperException extends RuntimeException {
    public PersonIsKeeperException() {
        super("Вы являетесь хранителем на данном курсе");
    }
}
