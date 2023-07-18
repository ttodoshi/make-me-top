package org.example.exception.classes.requestEX;

import org.example.model.courserequest.CourseRegistrationRequestStatusType;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(CourseRegistrationRequestStatusType status) {
        super("Статус запроса " + status + " не обнаружен");
    }
}
