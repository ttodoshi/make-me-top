package org.example.person.exception.explorer;

public class PersonIsExplorerException extends RuntimeException {
    public PersonIsExplorerException() {
        super("Вы являетесь исследователем на данном курсе");
    }
}

