package org.example.picture.exception.picture;

public class PictureNotFoundException extends RuntimeException {
    public PictureNotFoundException() {
        super("Изображение не найдено");
    }
}
