package org.example.service;

public interface CourseProgressService {
    boolean isCourseOpenedForAuthenticatedPerson(Integer courseId);

    boolean isAuthenticatedPersonCurrentlyStudying(Integer galaxyId);
}
