package org.example.service;

import org.example.dto.courseprogress.CourseWithThemesProgressDto;
import org.example.dto.courseprogress.CoursesStateDto;

import java.util.Map;

public interface CourseProgressService {
    CoursesStateDto getCoursesProgressForCurrentUser(Integer galaxyId);

    CourseWithThemesProgressDto getThemesProgressByCourseId(Integer courseId);

    boolean hasUncompletedParents(Integer personId, Integer systemId);

    Map<String, String> leaveCourse(Integer courseId);
}
