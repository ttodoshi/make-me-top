package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.model.Person;
import org.example.model.role.AuthenticationRoleType;
import org.example.model.role.CourseRoleType;
import org.example.model.role.GeneralRoleType;
import org.example.repository.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseRepository courseRepository;
    private final HomeworkRepository homeworkRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    @Override
    public boolean hasAnyGeneralRole(GeneralRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    @Override
    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    @Override
    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    @Override
    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseRepository.getCourseIdByThemeId(themeId)
                        .orElseThrow(() -> new CourseThemeNotFoundException(themeId)),
                role
        );
    }

    @Override
    public boolean hasAnyCourseRoleByGroupId(Integer groupId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.findById(groupId)
                        .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId)).getCourseId(),
                role
        );
    }

    @Override
    public boolean hasAnyCourseRoleByHomeworkId(Integer homeworkId, CourseRoleType role) {
        return hasAnyCourseRoleByThemeId(
                homeworkRepository.findById(homeworkId).orElseThrow(() -> new HomeworkNotFoundException(homeworkId)).getCourseThemeId(),
                role
        );
    }
}
