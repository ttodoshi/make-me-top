package org.example.course.service;

import org.example.course.dto.course.CourseDetailedDto;
import org.example.course.dto.course.CourseDto;
import org.example.course.dto.course.UpdateCourseDto;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface CourseService {
    CourseDto findCourseByCourseId(Long courseId);

    CourseDetailedDto findCourseByCourseIdDetailed(String authorizationHeader, Authentication authentication, Long courseId);

    Map<Long, CourseDto> findCoursesByCourseIdIn(List<Long> courseIds);

    CourseDto updateCourse(String authorizationHeader, Long galaxyId, Long courseId, UpdateCourseDto course);

    void deleteCourse(Long courseId);
}
