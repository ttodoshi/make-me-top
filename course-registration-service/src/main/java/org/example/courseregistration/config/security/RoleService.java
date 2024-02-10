package org.example.courseregistration.config.security;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.enums.AuthenticationRoleType;
import org.example.courseregistration.enums.CourseRoleType;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.service.ExplorerService;
import org.example.courseregistration.service.KeeperService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;

    public boolean hasAnyAuthenticationRole(Collection<? extends GrantedAuthority> authorities, AuthenticationRoleType role) {
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }

    public boolean hasAnyCoursesRole(String authorizationHeader, Long authenticatedPersonId, List<Long> courseIds, CourseRoleType role) {
        if (role.equals(CourseRoleType.EXPLORER)) {
            return courseIds.stream().allMatch(cId ->
                    explorerService.findExplorersByPersonIdAndGroupCourseIdIn(
                            authorizationHeader, authenticatedPersonId, courseIds
                    ).containsKey(cId));
        } else {
            return courseIds.stream().allMatch(cId ->
                    keeperService.findKeepersByPersonIdAndGroupCourseIdIn(
                            authorizationHeader, authenticatedPersonId, courseIds
                    ).containsKey(cId));
        }
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByRequestIds(String authorizationHeader, Long authenticatedPersonId, List<Long> requestIds, CourseRoleType role) {
        List<Long> courseIds = courseRegistrationRequestRepository
                .findCourseRegistrationRequestsByRequestIdIn(requestIds)
                .stream()
                .map(CourseRegistrationRequest::getCourseId)
                .collect(Collectors.toList());
        return hasAnyCoursesRole(authorizationHeader, authenticatedPersonId, courseIds, role);
    }
}
