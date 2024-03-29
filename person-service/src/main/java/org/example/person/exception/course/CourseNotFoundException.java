package org.example.person.exception.course;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException() {
        super("Система не обнаружила у себя в памяти запрошенный курс");
    }

    public CourseNotFoundException(Long courseId) {
        super("Система не обнаружила у себя в памяти курс " + courseId);
    }
}
