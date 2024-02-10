package org.example.picture.exception.picture;

public class PictureNotValidException extends RuntimeException {
    public PictureNotValidException() {
        super("Изображение не подходит");
    }
}
