package org.example.courseregistration.exception.classes.request;

public class PersonIsKeeperException extends RuntimeException {
    public PersonIsKeeperException() {
        super("Вы являетесь хранителем на данном курсе");
    }
}

