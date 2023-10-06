package org.example.exception.classes.courseEX;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException() {
        super("Система не обнаружила у себя в памяти запрошенный курс");
    }
}
