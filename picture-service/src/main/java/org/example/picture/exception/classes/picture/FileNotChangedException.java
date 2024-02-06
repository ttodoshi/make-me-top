package org.example.picture.exception.classes.picture;

public class FileNotChangedException extends RuntimeException {
    public FileNotChangedException() {
        super("Действие с файлом не выполнено");
    }
}
