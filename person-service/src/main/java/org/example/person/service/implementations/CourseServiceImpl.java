package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.course.CourseDto;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.service.CourseService;
import org.example.person.repository.CourseRatingRepository;
import org.example.person.repository.CourseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseRatingRepository courseRatingRepository;

    @Override
    public List<CourseWithRatingDto> getCoursesRating(List<Integer> courseIds) {
        Map<Integer, CourseDto> courses = courseRepository.findCoursesByCourseIdIn(courseIds);
        Map<Integer, Double> courseRatings = courseRatingRepository
                .findCourseRatingsByCourseIdIn(courseIds);
        return courses.values()
                .stream()
                .map(c -> new CourseWithRatingDto(
                        c.getCourseId(),
                        c.getTitle(),
                        courseRatings.getOrDefault(c.getCourseId(), 0.0)
                )).collect(Collectors.toList());
    }
}
