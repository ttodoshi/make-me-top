package org.example.planet.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.dto.event.CourseThemeCreateEvent;
import org.example.course.dto.event.CourseThemeUpdateEvent;
import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.PlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class CourseThemeAspect {
    private final KafkaTemplate<Long, Object> createThemeKafkaTemplate;
    private final KafkaTemplate<Long, Object> updateThemeKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteThemeKafkaTemplate;

    public CourseThemeAspect(
            @Qualifier("createCourseThemeKafkaTemplate") KafkaTemplate<Long, Object> createThemeKafkaTemplate,
            @Qualifier("updateCourseThemeKafkaTemplate") KafkaTemplate<Long, Object> updateThemeKafkaTemplate,
            KafkaTemplate<Long, Long> deleteThemeKafkaTemplate) {
        this.createThemeKafkaTemplate = createThemeKafkaTemplate;
        this.updateThemeKafkaTemplate = updateThemeKafkaTemplate;
        this.deleteThemeKafkaTemplate = deleteThemeKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.planet.service.PlanetService.createPlanets(..)) " +
            "&& args(authorizationHeader, systemId, planets)", argNames = "authorizationHeader, systemId, planets")
    public void createCourseThemesPointcut(String authorizationHeader, Long systemId, List<CreatePlanetDto> planets) {
    }

    @AfterReturning(pointcut = "createCourseThemesPointcut(authorizationHeader, systemId, planets)", returning = "result", argNames = "authorizationHeader, systemId, planets, result")
    public void createCourseThemesAfterPlanetsCreated(String authorizationHeader, Long systemId, List<CreatePlanetDto> planets, List<Long> result) {
        for (int i = 0; i < planets.size(); i++) {
            createThemeKafkaTemplate.send(
                    "createCourseThemeTopic",
                    systemId,
                    new CourseThemeCreateEvent(
                            result.get(i),
                            planets.get(i).getPlanetName(),
                            planets.get(i).getDescription(),
                            planets.get(i).getContent(),
                            planets.get(i).getPlanetNumber()
                    )
            );
        }
    }

    @Pointcut(value = "execution(* org.example.planet.service.PlanetService.updatePlanet(..)) " +
            "&& args(authorizationHeader, planetId, planet)", argNames = "authorizationHeader, planetId, planet")
    public void updateCourseThemeTitlePointcut(String authorizationHeader, Long planetId, UpdatePlanetDto planet) {
    }

    @AfterReturning(pointcut = "updateCourseThemeTitlePointcut(authorizationHeader,planetId, planet)", returning = "result", argNames = "authorizationHeader,planetId, planet, result")
    public void updateCourseThemeAfterPlanetUpdated(String authorizationHeader, Long planetId, UpdatePlanetDto planet, PlanetDto result) {
        updateThemeKafkaTemplate.send(
                "updateCourseThemeTopic",
                planetId,
                new CourseThemeUpdateEvent(
                        planet.getPlanetName(),
                        planet.getPlanetNumber(),
                        planet.getSystemId()
                )
        );
    }

    @Pointcut(value = "execution(* org.example.planet.service.PlanetService.deletePlanetById(..)) " +
            "&& args(planetId)", argNames = "planetId")
    public void deleteCourseThemePointcut(Long planetId) {
    }

    @AfterReturning(pointcut = "deleteCourseThemePointcut(planetId)", returning = "result", argNames = "planetId, result")
    public void deleteCourseThemeAfterPlanetDeleted(Long planetId, MessageDto result) {
        deleteThemeKafkaTemplate.send("deleteCourseThemeTopic", planetId);
    }
}
