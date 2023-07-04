package org.example.exception.classes.courseEX;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException() {
        super("Система не обнажужила у себя в памяти данный курс");
    }
}
