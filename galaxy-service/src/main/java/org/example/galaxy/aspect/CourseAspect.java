package org.example.galaxy.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.dto.event.CourseCreateEvent;
import org.example.galaxy.dto.system.CreateStarSystemDto;
import org.example.galaxy.dto.system.StarSystemDto;
import org.example.galaxy.dto.system.UpdateStarSystemDto;
import org.example.galaxy.exception.classes.galaxy.GalaxyNotFoundException;
import org.example.galaxy.exception.classes.orbit.OrbitNotFoundException;
import org.example.galaxy.exception.classes.system.SystemNotFoundException;
import org.example.galaxy.repository.GalaxyRepository;
import org.example.galaxy.repository.OrbitRepository;
import org.example.galaxy.repository.StarSystemRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CourseAspect {
    private final GalaxyRepository galaxyRepository;
    private final OrbitRepository orbitRepository;
    private final StarSystemRepository starSystemRepository;
    private final KafkaTemplate<Long, Long> deleteCourseKafkaTemplate;
    private final KafkaTemplate<Long, Long> deletePlanetsKafkaTemplate;
    private final KafkaTemplate<String, Object> createCourseKafkaTemplate;
    private final KafkaTemplate<Long, String> updateCourseKafkaTemplate;

    public CourseAspect(
            GalaxyRepository galaxyRepository,
            OrbitRepository orbitRepository,
            StarSystemRepository starSystemRepository,
            @Qualifier("deleteCourseKafkaTemplate") KafkaTemplate<Long, Long> deleteCourseKafkaTemplate,
            @Qualifier("deletePlanetsKafkaTemplate") KafkaTemplate<Long, Long> deletePlanetsKafkaTemplate,
            KafkaTemplate<String, Object> createCourseKafkaTemplate,
            KafkaTemplate<Long, String> updateCourseKafkaTemplate) {
        this.galaxyRepository = galaxyRepository;
        this.orbitRepository = orbitRepository;
        this.starSystemRepository = starSystemRepository;
        this.deleteCourseKafkaTemplate = deleteCourseKafkaTemplate;
        this.deletePlanetsKafkaTemplate = deletePlanetsKafkaTemplate;
        this.createCourseKafkaTemplate = createCourseKafkaTemplate;
        this.updateCourseKafkaTemplate = updateCourseKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.galaxy.service.GalaxyService.deleteGalaxy(..)) " +
            "&& args(galaxyId)", argNames = "galaxyId")
    public void deleteCoursesAndThemesWhenGalaxyDeletedPointcut(Long galaxyId) {
    }

    @Before(value = "deleteCoursesAndThemesWhenGalaxyDeletedPointcut(galaxyId)", argNames = "galaxyId")
    public void deleteCoursesAndThemesBeforeGalaxyDeleted(Long galaxyId) {
        galaxyRepository.findById(galaxyId)
                .orElseThrow(() -> new GalaxyNotFoundException(galaxyId))
                .getOrbits()
                .stream()
                .flatMap(g -> g.getSystems().stream())
                .forEach(s -> clearCourseAndPlanets(s.getSystemId()));
    }

    @Pointcut(value = "execution(* org.example.galaxy.service.OrbitService.deleteOrbit(..)) " +
            "&& args(orbitId)", argNames = "orbitId")
    public void deleteCoursesAndThemesWhenOrbitDeletedPointcut(Long orbitId) {
    }

    @Before(value = "deleteCoursesAndThemesWhenOrbitDeletedPointcut(orbitId)", argNames = "orbitId")
    public void deleteCoursesAndThemesBeforeOrbitDeleted(Long orbitId) {
        orbitRepository.findById(orbitId)
                .orElseThrow(() -> new OrbitNotFoundException(orbitId))
                .getSystems()
                .forEach(s -> clearCourseAndPlanets(s.getSystemId()));
    }

    @Pointcut(value = "execution(* org.example.galaxy.service.StarSystemService.deleteSystem(..)) " +
            "&& args(systemId)", argNames = "systemId")
    public void deleteCourseAndThemeWhenSystemDeletedPointcut(Long systemId) {
    }

    @Before(value = "deleteCourseAndThemeWhenSystemDeletedPointcut(systemId)", argNames = "systemId")
    public void deleteCourseAndThemeBeforeSystemDeleted(Long systemId) {
        if (!starSystemRepository.existsById(systemId))
            throw new SystemNotFoundException(systemId);
        clearCourseAndPlanets(systemId);
    }

    private void clearCourseAndPlanets(Long systemId) {
        deletePlanetsKafkaTemplate.send("deletePlanetsTopic", systemId);
        deleteCourseKafkaTemplate.send("deleteCourseTopic", systemId);
    }

    @Pointcut(value = "execution(* org.example.galaxy.service.StarSystemService.createSystem(..)) " +
            "&& args(orbitId, systemRequest)", argNames = "orbitId, systemRequest")
    public void createCourseWhenSystemCreatedPointcut(Long orbitId, CreateStarSystemDto systemRequest) {
    }

    @AfterReturning(pointcut = "createCourseWhenSystemCreatedPointcut(orbitId, systemRequest)", returning = "result", argNames = "orbitId, systemRequest, result")
    public void createCourseAfterSystemCreated(Long orbitId, CreateStarSystemDto systemRequest, Long result) {
        createCourseKafkaTemplate.send(
                "createCourseTopic",
                new CourseCreateEvent(
                        result,
                        systemRequest.getSystemName(),
                        systemRequest.getDescription()
                )
        );
    }

    @Pointcut(value = "execution(* org.example.galaxy.service.StarSystemService.updateSystem(..)) " +
            "&& args(systemId, starSystem)", argNames = "systemId, starSystem")
    public void updateCourseTitlePointcut(Long systemId, UpdateStarSystemDto starSystem) {
    }

    @AfterReturning(pointcut = "updateCourseTitlePointcut(systemId, starSystem)", returning = "result", argNames = "systemId, starSystem, result")
    public void updateCourseTitlePointcutAfterSystemUpdated(Long systemId, UpdateStarSystemDto starSystem, StarSystemDto result) {
        updateCourseKafkaTemplate.send("updateCourseTopic", systemId, result.getSystemName());
    }
}
