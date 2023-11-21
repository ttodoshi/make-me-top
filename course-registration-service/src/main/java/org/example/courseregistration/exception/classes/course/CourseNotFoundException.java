package org.example.courseregistration.exception.classes.course;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(Integer courseId) {
        super("Система не обнаружила у себя в памяти курс " + courseId);
    }
}
