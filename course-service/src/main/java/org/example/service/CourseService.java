package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.AddKeeperRequest;
import org.example.dto.CourseUpdateRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Course;
import org.example.model.Keeper;
import org.example.repository.CourseRepository;
import org.example.repository.KeeperRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    private final Logger logger = Logger.getLogger(CourseService.class.getName());

    public Course getCourse(Integer courseId) {
        return courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(CourseUpdateRequest course, Integer courseId) {
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
        updatedCourse.setDescription(course.getDescription());
        updatedCourse.setLastModified(new Date());
        updatedCourse.setTitle(course.getTitle());
        return courseRepository.save(updatedCourse);
    }

    public Map<String, String> deleteCourse(Integer courseId) {
        try {
            courseRepository.deleteById(courseId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Курс " + courseId + " был удалён");
            return response;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new CourseNotFoundException();
        }
    }

    public Keeper setKeeperToCourse(Integer courseId, AddKeeperRequest addKeeperRequest) {
        Keeper keeper = new Keeper();
        keeper.setCourseId(courseId);
        keeper.setPersonId(addKeeperRequest.getPersonId());
        keeper.setStartDate(new Date());
        return keeperRepository.save(keeper);
    }
}
