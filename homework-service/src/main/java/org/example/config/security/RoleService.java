package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;
import org.example.dto.explorer.ExplorerDto;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.exception.classes.homeworkEX.HomeworkRequestNotFound;
import org.example.repository.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRepository homeworkRepository;
    private final HomeworkRequestRepository homeworkRequestRepository;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseThemeRepository.findById(themeId)
                        .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                        .getCourseId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByHomeworkId(Integer homeworkId, CourseRoleType role) {
        return hasAnyCourseRoleByThemeId(
                homeworkRepository.findById(homeworkId)
                        .orElseThrow(() -> new HomeworkNotFoundException(homeworkId))
                        .getCourseThemeId(),
                role
        );
    }

    // TODO
    public boolean hasAnyCourseRoleByHomeworkIds(List<Integer> homeworkIds, CourseRoleType role) {
        return homeworkIds.stream()
                .allMatch(hId -> hasAnyCourseRoleByHomeworkId(hId, role));
    }

    public boolean hasAnyCourseRoleByHomeworkRequestId(Integer homeworkRequestId, CourseRoleType role) {
        return hasAnyCourseRoleByHomeworkId(
                homeworkRequestRepository.findById(homeworkRequestId).orElseThrow(
                        () -> new HomeworkRequestNotFound(homeworkRequestId)
                ).getHomeworkId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByGroupId(Integer groupId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.findById(groupId)
                        .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId)).getCourseId(),
                role
        );
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByExplorerIds(List<Integer> explorerIds, CourseRoleType role) {
        Map<Integer, ExplorerDto> explorers = explorerRepository.findExplorersByExplorerIdIn(explorerIds);
        Map<Integer, Integer> courseIds = explorerGroupRepository.findExplorerGroupsCourseIdByGroupIdIn(
                explorers.values().stream().map(ExplorerDto::getGroupId).collect(Collectors.toList())
        );
        return courseIds.values().stream()
                .distinct()
                .allMatch(cId -> hasAnyCourseRole(cId, role)); // TODO
    }
}
