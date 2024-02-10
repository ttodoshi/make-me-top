package org.example.course.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.course.exception.course.CourseNotFoundException;
import org.example.course.model.CourseTheme;
import org.example.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class DeleteRelatedDataAspect {
    private final CourseRepository courseRepository;

    private final KafkaTemplate<Long, Long> deleteKeepersKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteRequestsKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteExplorersProgressKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteHomeworksKafkaTemplate;

    public DeleteRelatedDataAspect(
            CourseRepository courseRepository,
            @Qualifier("deleteKeepersKafkaTemplate") KafkaTemplate<Long, Long> deleteKeepersKafkaTemplate,
            @Qualifier("deleteRequestsKafkaTemplate") KafkaTemplate<Long, Long> deleteRequestsKafkaTemplate,
            @Qualifier("deleteExplorersProgressKafkaTemplate") KafkaTemplate<Long, Long> deleteExplorersProgressKafkaTemplate,
            @Qualifier("deleteHomeworksKafkaTemplate") KafkaTemplate<Long, Long> deleteHomeworksKafkaTemplate) {
        this.courseRepository = courseRepository;
        this.deleteKeepersKafkaTemplate = deleteKeepersKafkaTemplate;
        this.deleteRequestsKafkaTemplate = deleteRequestsKafkaTemplate;
        this.deleteExplorersProgressKafkaTemplate = deleteExplorersProgressKafkaTemplate;
        this.deleteHomeworksKafkaTemplate = deleteHomeworksKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.course.service.CourseService.deleteCourse(..)) " +
            "&& args(courseId)", argNames = "courseId")
    public void deleteDataRelatedToCoursePointcut(Long courseId) {
    }

    @Before(value = "deleteDataRelatedToCoursePointcut(courseId)", argNames = "courseId")
    public void deleteDataRelatedToCourseBeforeCourseDeletion(Long courseId) {
        this.deleteDataRelatedToThemes(
                courseRepository.findById(courseId)
                        .orElseThrow(() -> new CourseNotFoundException(courseId))
                        .getCourseThemes()
                        .stream().map(CourseTheme::getCourseThemeId)
                        .collect(Collectors.toList())
        );
        deleteRequestsByCourseId(courseId);
        deleteKeepersByCourseId(courseId);
    }

    private void deleteDataRelatedToThemes(List<Long> themeIds) {
        themeIds.forEach(this::deleteDataRelatedToTheme);
    }

    private void deleteRequestsByCourseId(Long courseId) {
        deleteRequestsKafkaTemplate.send("deleteCourseRegistrationRequestsTopic", courseId);
    }

    private void deleteKeepersByCourseId(Long courseId) {
        deleteKeepersKafkaTemplate.send("deleteKeepersTopic", courseId);
    }

    @Pointcut(value = "execution(* org.example.course.service.CourseThemeService.deleteCourseTheme(..)) " +
            "&& args(courseThemeId)", argNames = "courseThemeId")
    public void deleteDataRelatedToCourseThemePointcut(Long courseThemeId) {
    }

    @AfterReturning(pointcut = "deleteDataRelatedToCourseThemePointcut(courseThemeId)", argNames = "courseThemeId")
    public void deleteDataRelatedToCourseThemeAfterCourseThemeDeletion(Long courseThemeId) {
        deleteDataRelatedToTheme(courseThemeId);
    }

    private void deleteDataRelatedToTheme(Long themeId) {
        deleteHomeworksByThemeId(themeId);
        deleteExplorersProgressByThemeId(themeId);
    }

    private void deleteHomeworksByThemeId(Long themeId) {
        deleteHomeworksKafkaTemplate.send("deleteHomeworksTopic", themeId);
    }

    private void deleteExplorersProgressByThemeId(Long themeId) {
        deleteExplorersProgressKafkaTemplate.send("deleteExplorersProgressTopic", themeId);
    }
}
