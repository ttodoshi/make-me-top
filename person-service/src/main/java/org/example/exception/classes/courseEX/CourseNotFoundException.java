package org.example.exception.classes.courseEX;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException() {
        super("Система не обнаружила у себя в памяти запрошенный курс");
    }

    public CourseNotFoundException(Integer courseId) {
        super("Система не обнаружила у себя в памяти курс " + courseId);
    }
}
