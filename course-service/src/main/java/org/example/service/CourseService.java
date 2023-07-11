package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.course.CourseUpdateRequest;
import org.example.dto.course.CourseWithKeepers;
import org.example.dto.keeper.AddKeeperRequest;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.model.Course;
import org.example.model.Keeper;
import org.example.repository.CourseRepository;
import org.example.repository.KeeperRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    private final ModelMapper mapper;

    public CourseWithKeepers getCourse(Integer courseId) {
        CourseWithKeepers courseWithKeepers = mapper.map(
                courseRepository.findById(courseId)
                        .orElseThrow(CourseNotFoundException::new),
                CourseWithKeepers.class);
        courseWithKeepers.setKeepers(keeperRepository.getKeepersByCourseId(courseId));
        return courseWithKeepers;
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
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

    public Keeper setKeeperToCourse(Integer courseId, AddKeeperRequest addKeeperRequest) {
        Keeper keeper = new Keeper();
        keeper.setCourseId(courseId);
        keeper.setPersonId(addKeeperRequest.getPersonId());
        keeper.setStartDate(new Date());
        return keeperRepository.save(keeper);
    }
}
