package org.example.person.exception.classes.explorer;

public class PersonIsExplorerException extends RuntimeException {
    public PersonIsExplorerException() {
        super("Вы являетесь исследователем на данном курсе");
    }
}

