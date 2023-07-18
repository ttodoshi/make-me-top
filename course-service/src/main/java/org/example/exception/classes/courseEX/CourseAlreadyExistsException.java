package org.example.exception.classes.courseEX;

public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String title) {
        super("По данным бортового компьютера курс '" + title + "' уже существует");
    }
}
