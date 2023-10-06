package org.example.service.implementations;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseDto;
import org.example.dto.course.CourseWithRatingDto;
import org.example.repository.CourseRatingRepository;
import org.example.repository.CourseRepository;
import org.example.service.CourseService;
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
                        courseRatings.get(c.getCourseId())
                )).collect(Collectors.toList());
    }
}
