package org.example.course.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PlanetAspect {
    private final KafkaTemplate<Long, String> updatePlanetKafkaTemplate;

    public PlanetAspect(@Qualifier("updatePlanetKafkaTemplate") KafkaTemplate<Long, String> updatePlanetKafkaTemplate) {
        this.updatePlanetKafkaTemplate = updatePlanetKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.course.service.CourseThemeService.updateCourseTheme(..)) " +
            "&& args(courseThemeId, courseTheme)", argNames = "courseThemeId, courseTheme")
    public void updatePlanetNamePointcut(Long courseThemeId, UpdateCourseThemeDto courseTheme) {
    }

    @AfterReturning(pointcut = "updatePlanetNamePointcut(courseThemeId, courseTheme)", argNames = "courseThemeId, courseTheme")
    public void updatePlanetNameAfterCourseThemeNameUpdated(Long courseThemeId, UpdateCourseThemeDto courseTheme) {
        updatePlanetKafkaTemplate.send("updatePlanetTopic", courseThemeId, courseTheme.getTitle());
    }
}
