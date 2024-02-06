package org.example.picture.exception.classes.picture;

public class PictureNotValidException extends RuntimeException {
    public PictureNotValidException() {
        super("Изображение не подходит");
    }
}
