package org.example.picture.exception.classes.picture;

public class PictureNotFoundException extends RuntimeException {
    public PictureNotFoundException() {
        super("Изображение не найдено");
    }
}
