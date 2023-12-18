package org.example.courseregistration.exception.classes.courserequest;

public class PersonIsKeeperException extends RuntimeException {
    public PersonIsKeeperException() {
        super("Вы являетесь хранителем на данном курсе");
    }
}

