package org.example.courseregistration.service;

import java.util.List;
import java.util.Set;

public interface CourseProgressService {
    boolean isCourseOpenedForAuthenticatedPerson(String authorizationHeader, Long courseId);

    boolean isAuthenticatedPersonCurrentlyStudying(String authorizationHeader, Long authenticatedPersonId);

    Set<Long> getExplorersWithFinalAssessment(String authorizationHeader, List<Long> explorerIds);
}
