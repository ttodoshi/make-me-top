package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkRequestNotFound;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.model.Person;
import org.example.model.role.AuthenticationRoleType;
import org.example.model.role.CourseRoleType;
import org.example.repository.*;
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.courseregistration.CourseRegistrationRequestRepository;
import org.example.repository.homework.HomeworkRequestRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean hasAnyCourseRoleByRequestId(Integer requestId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseRegistrationRequestRepository.findById(requestId)
                        .orElseThrow(() -> new RequestNotFoundException(requestId)).getCourseId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByExplorerId(Integer explorerId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerRepository.findById(explorerId)
                        .orElseThrow(() -> new ExplorerNotFoundException(explorerId)).getCourseId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseRepository.getCourseIdByThemeId(themeId)
                        .orElseThrow(() -> new CourseThemeNotFoundException(themeId)),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkId(Integer homeworkId, CourseRoleType role) {
        return hasAnyCourseRoleByThemeId(
                courseThemeRepository.getCourseThemeIdByHomeworkId(homeworkId)
                        .orElseThrow(() -> new HomeworkNotFoundException(homeworkId)),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkRequestId(Integer homeworkRequestId, CourseRoleType role) {
        return hasAnyCourseRoleByHomeworkId(
                homeworkRequestRepository.findById(homeworkRequestId).orElseThrow(
                        () -> new HomeworkRequestNotFound(homeworkRequestId)
                ).getHomeworkId(),
                role
        );
    }
}
