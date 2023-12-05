package org.example.course.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.example.planet.dto.event.PlanetUpdateEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PlanetAspect {
    private final KafkaTemplate<Long, Object> updatePlanetKafkaTemplate;

    public PlanetAspect(@Qualifier("updatePlanetKafkaTemplate") KafkaTemplate<Long, Object> updatePlanetKafkaTemplate) {
        this.updatePlanetKafkaTemplate = updatePlanetKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.course.service.CourseThemeService.updateCourseTheme(..)) " +
            "&& args(courseThemeId, courseTheme)", argNames = "courseThemeId, courseTheme")
    public void updatePlanetPointcut(Long courseThemeId, UpdateCourseThemeDto courseTheme) {
    }

    @AfterReturning(pointcut = "updatePlanetPointcut(courseThemeId, courseTheme)", argNames = "courseThemeId, courseTheme")
    public void updatePlanetAfterCourseThemeNameUpdated(Long courseThemeId, UpdateCourseThemeDto courseTheme) {
        updatePlanetKafkaTemplate.send(
                "updatePlanetTopic",
                courseThemeId,
                new PlanetUpdateEvent(
                        courseTheme.getTitle(),
                        courseTheme.getCourseThemeNumber(),
                        courseTheme.getCourseId()
                ));
    }
}
