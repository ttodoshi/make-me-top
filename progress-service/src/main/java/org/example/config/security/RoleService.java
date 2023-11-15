package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;
import org.example.dto.PersonDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.planetEX.PlanetNotFoundException;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.PlanetRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final PlanetRepository planetRepository;

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

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByExplorerId(Integer explorerId, CourseRoleType role) {
        return hasAnyCourseRole(
                explorerGroupRepository.getReferenceById(
                                explorerRepository.findById(explorerId)
                                        .orElseThrow(() -> new ExplorerNotFoundException(explorerId))
                                        .getGroupId()
                        )
                        .getCourseId(),
                role
        );
    }

    public boolean hasAnyCourseRoleByThemeId(Integer themeId, CourseRoleType role) {
        return hasAnyCourseRole(
                planetRepository.findById(themeId)
                        .orElseThrow(() -> new PlanetNotFoundException(themeId))
                        .getSystemId(),
                role
        );
    }
}
