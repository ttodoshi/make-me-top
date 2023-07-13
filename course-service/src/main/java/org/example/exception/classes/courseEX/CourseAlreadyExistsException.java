package org.example.exception.classes.courseEX;

public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException() {
        super("По данным бортового компьютера данный курс уже существует");
    }
}
