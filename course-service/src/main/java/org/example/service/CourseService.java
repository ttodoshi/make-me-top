package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.course.CourseUpdateRequest;
import org.example.dto.course.CourseWithKeepers;
import org.example.dto.keeper.AddKeeperRequest;
import org.example.dto.starsystem.StarSystemDTO;
import org.example.exception.classes.connectEX.ConnectException;
import org.example.exception.classes.courseEX.CourseAlreadyExistsException;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.galaxyEX.GalaxyNotFoundException;
import org.example.exception.classes.personEX.PersonNotFoundException;
import org.example.model.Course;
import org.example.model.Keeper;
import org.example.repository.CourseRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final PersonRepository personRepository;

    private final ModelMapper mapper;

    @Setter
    private String token;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;

    public CourseWithKeepers getCourse(Integer courseId) {
        CourseWithKeepers courseWithKeepers = mapper.map(
                courseRepository.findById(courseId)
                        .orElseThrow(() -> new CourseNotFoundException(courseId)),
                CourseWithKeepers.class);
        courseWithKeepers.setKeepers(keeperRepository.getKeepersByCourseId(courseId));
        return courseWithKeepers;
    }

    public List<Course> getCoursesByGalaxyId(Integer galaxyId) {
        List<Integer> systems = Arrays.stream(getSystemsIdByGalaxyId(galaxyId))
                .mapToInt(StarSystemDTO::getSystemId)
                .boxed().collect(Collectors.toList());
        return courseRepository.findAll().stream().filter(
                c -> systems.contains(c.getCourseId())
        ).collect(Collectors.toList());
    }

    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Integer galaxyId, Integer courseId, CourseUpdateRequest course) {
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
        boolean courseExists = Arrays.stream(getSystemsIdByGalaxyId(galaxyId))
                .anyMatch(s -> s.getSystemName().equals(updatedCourse.getTitle()));
        if (courseExists)
            throw new CourseAlreadyExistsException(updatedCourse.getTitle());
        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());
        updatedCourse.setLastModified(new Date());
        return courseRepository.save(updatedCourse);
    }

    private StarSystemDTO[] getSystemsIdByGalaxyId(Integer galaxyId) {
        WebClient webClient = WebClient.create(GALAXY_APP_URL);
        return webClient.get()
                .uri("/galaxy/" + galaxyId + "/system/")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, e -> Mono.error(new GalaxyNotFoundException(galaxyId)))
                .bodyToMono(StarSystemDTO[].class)
                .doOnError(ConnectException::new)
                .block();
    }

    public Keeper setKeeperToCourse(Integer courseId, AddKeeperRequest addKeeperRequest) {
        Keeper keeper = new Keeper();
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        if (!personRepository.existsById(addKeeperRequest.getPersonId()))
            throw new PersonNotFoundException();
        keeper.setCourseId(courseId);
        keeper.setPersonId(addKeeperRequest.getPersonId());
        keeper.setStartDate(new Date());
        return keeperRepository.save(keeper);
    }
}
