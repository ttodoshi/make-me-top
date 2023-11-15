package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;
import org.example.dto.person.PersonDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.model.CourseTheme;
import org.example.repository.CourseThemeRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseThemeRepository courseThemeRepository;

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
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    @Override
    public boolean hasAnyCoursesRole(List<Integer> courseIds, CourseRoleType role) {
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER)) {
            return courseIds.stream().allMatch(cId ->
                    explorerRepository.findExplorersByPersonIdAndGroupCourseIdIn(person.getPersonId(), courseIds)
                            .getExplorersWithCourseIdMapMap()
                            .containsKey(cId));
        } else {
            return courseIds.stream().allMatch(cId ->
                    keeperRepository.findKeepersByPersonIdAndGroupCourseIdIn(person.getPersonId(), courseIds)
                            .getKeepersWithCourseIdMapMap()
                            .containsKey(cId));
        }
    }

    @Override
    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseThemeRepository.findById(themeId)
                        .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                        .getCourseId(),
                role
        );
    }

    @Override
    public boolean hasAnyCourseRoleByThemeIds(List<Integer> themeIds, CourseRoleType role) {
        return hasAnyCoursesRole(
                courseThemeRepository.findCourseThemesByCourseThemeIdIn(themeIds)
                        .stream()
                        .map(CourseTheme::getCourseId)
                        .collect(Collectors.toList()),
                role);
    }
}
