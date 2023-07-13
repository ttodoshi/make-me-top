package org.example.exception.classes.coursethemeEX;

public class CourseThemeNotFoundException extends RuntimeException {
    public CourseThemeNotFoundException() {
        super("Система не обнаружила у себя в памяти данную тему из курса");
    }
}
