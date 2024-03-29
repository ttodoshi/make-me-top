package org.example.course.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.dto.course.UpdateCourseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StarSystemAspect {
    private final KafkaTemplate<Long, String> updateSystemKafkaTemplate;

    public StarSystemAspect(@Qualifier("updateSystemKafkaTemplate") KafkaTemplate<Long, String> updateSystemKafkaTemplate) {
        this.updateSystemKafkaTemplate = updateSystemKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.course.service.CourseService.updateCourse(..)) " +
            "&& args(authorizationHeader, galaxyId, courseId, course)", argNames = "authorizationHeader, galaxyId, courseId, course")
    public void updateSystemNamePointcut(String authorizationHeader, Long galaxyId, Long courseId, UpdateCourseDto course) {
    }

    @AfterReturning(pointcut = "updateSystemNamePointcut(authorizationHeader, galaxyId, courseId, course)", argNames = "authorizationHeader, galaxyId, courseId, course")
    public void updateSystemNameAfterUpdateCourseTitle(String authorizationHeader, Long galaxyId, Long courseId, UpdateCourseDto course) {
        updateSystemKafkaTemplate.send("updateSystemTopic", courseId, course.getTitle());
    }
}
