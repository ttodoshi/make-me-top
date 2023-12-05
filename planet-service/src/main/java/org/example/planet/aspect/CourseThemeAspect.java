package org.example.planet.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.dto.event.CourseThemeCreateEvent;
import org.example.planet.dto.message.MessageDto;
import org.example.planet.dto.planet.CreatePlanetDto;
import org.example.planet.dto.planet.PlanetDto;
import org.example.planet.dto.planet.UpdatePlanetDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class CourseThemeAspect {
    private final KafkaTemplate<Long, Object> createThemeKafkaTemplate;
    private final KafkaTemplate<Long, String> updateThemeKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteThemeKafkaTemplate;

    public CourseThemeAspect(KafkaTemplate<Long, Object> createThemeKafkaTemplate, KafkaTemplate<Long, String> updateThemeKafkaTemplate, KafkaTemplate<Long, Long> deleteThemeKafkaTemplate) {
        this.createThemeKafkaTemplate = createThemeKafkaTemplate;
        this.updateThemeKafkaTemplate = updateThemeKafkaTemplate;
        this.deleteThemeKafkaTemplate = deleteThemeKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.planet.service.PlanetService.createPlanets(..)) " +
            "&& args(systemId, planets)", argNames = "systemId, planets")
    public void createCourseThemesPointcut(Long systemId, List<CreatePlanetDto> planets) {
    }

    @AfterReturning(pointcut = "createCourseThemesPointcut(systemId, planets)", returning = "result", argNames = "systemId, planets, result")
    public void createCourseThemesAfterPlanetsCreated(Long systemId, List<CreatePlanetDto> planets, List<PlanetDto> result) {
        for (int i = 0; i < planets.size(); i++) {
            createThemeKafkaTemplate.send(
                    "createCourseThemeTopic",
                    systemId,
                    new CourseThemeCreateEvent(
                            result.get(i).getPlanetId(),
                            result.get(i).getPlanetName(),
                            planets.get(i).getDescription(),
                            planets.get(i).getContent(),
                            result.get(i).getPlanetNumber()
                    )
            );
        }
    }

    @Pointcut(value = "execution(* org.example.planet.service.PlanetService.updatePlanet(..)) " +
            "&& args(planetId, planet)", argNames = "planetId, planet")
    public void updateCourseThemeTitlePointcut(Long planetId, UpdatePlanetDto planet) {
    }

    @AfterReturning(pointcut = "updateCourseThemeTitlePointcut(planetId, planet)", returning = "result", argNames = "planetId, planet, result")
    public void updateCourseThemeTitleAfterPlanetUpdated(Long planetId, UpdatePlanetDto planet, PlanetDto result) {
        updateThemeKafkaTemplate.send("updateCourseThemeTopic", planetId, planet.getPlanetName());
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
