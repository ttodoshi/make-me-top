package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.dto.course.CourseUpdateRequest;
import org.example.dto.course.CourseWithKeepers;
import org.example.dto.keeper.AddKeeperRequest;
import org.example.dto.starsystem.StarSystemDTO;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;
    private final PersonRepository personRepository;

    private final RestTemplate restTemplate;

    private final ModelMapper mapper;

    @Setter
    private String token;
    @Value("${galaxy_app_url}")
    private String GALAXY_APP_URL;

    public CourseWithKeepers getCourse(Integer courseId) {
        CourseWithKeepers courseWithKeepers = mapper.map(
                courseRepository.findById(courseId)
                        .orElseThrow(CourseNotFoundException::new),
                CourseWithKeepers.class);
        courseWithKeepers.setKeepers(keeperRepository.getKeepersByCourseId(courseId));
        return courseWithKeepers;
    }

    public List<Course> getCoursesByGalaxyId(Integer galaxyId) {
        List<Integer> systems = getSystemsIdByGalaxyId(galaxyId)
                .stream()
                .mapToInt(StarSystemDTO::getSystemId)
                .boxed().collect(Collectors.toList());
        return courseRepository.findAll().stream().filter(
                c -> systems.contains(c.getCourseId())
        ).collect(Collectors.toList());
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Integer galaxyId, Integer courseId, CourseUpdateRequest course) {
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow(CourseNotFoundException::new);
        boolean courseExists = getSystemsIdByGalaxyId(galaxyId).stream().anyMatch(
                s -> s.getSystemName().equals(updatedCourse.getTitle())
        );
        if (courseExists)
            throw new CourseAlreadyExistsException();
        updatedCourse.setTitle(course.getTitle());
        updatedCourse.setDescription(course.getDescription());
        updatedCourse.setLastModified(new Date());
        return courseRepository.save(updatedCourse);
    }

    private List<StarSystemDTO> getSystemsIdByGalaxyId(Integer galaxyId) {
        try {
            return Arrays.stream(
                    Objects.requireNonNull(restTemplate.exchange(
                            GALAXY_APP_URL + "/galaxy/" + galaxyId + "/system/",
                            HttpMethod.GET,
                            new HttpEntity<>(createHeaders()),
                            StarSystemDTO[].class).getBody()
                    )
            ).collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            throw new GalaxyNotFoundException();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return headers;
    }

    public Keeper setKeeperToCourse(Integer courseId, AddKeeperRequest addKeeperRequest) {
        Keeper keeper = new Keeper();
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException();
        if (!personRepository.existsById(addKeeperRequest.getPersonId()))
            throw new PersonNotFoundException();
        keeper.setCourseId(courseId);
        keeper.setPersonId(addKeeperRequest.getPersonId());
        keeper.setStartDate(new Date());
        return keeperRepository.save(keeper);
    }
}
