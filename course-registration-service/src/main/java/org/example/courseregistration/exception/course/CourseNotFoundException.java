package org.example.courseregistration.exception.course;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(Long courseId) {
        super("Система не обнаружила у себя в памяти курс " + courseId);
    }
}
