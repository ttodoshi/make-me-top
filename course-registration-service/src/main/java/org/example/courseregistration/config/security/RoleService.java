package org.example.courseregistration.config.security;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.enums.AuthenticationRoleType;
import org.example.courseregistration.enums.CourseRoleType;
import org.example.courseregistration.exception.classes.request.RequestNotFoundException;
import org.example.courseregistration.model.CourseRegistrationRequest;
import org.example.courseregistration.repository.CourseRegistrationRequestRepository;
import org.example.courseregistration.repository.ExplorerRepository;
import org.example.courseregistration.repository.KeeperRepository;
import org.example.courseregistration.service.PersonService;
import org.example.grpc.PeopleService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;

    public boolean hasAnyAuthenticationRole(AuthenticationRoleType role) {
        for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(role.name()))
                return true;
        }
        return false;
    }


    public boolean hasAnyCourseRole(Integer courseId, CourseRoleType role) {
        PeopleService.Person person = personService.getAuthenticatedPerson();
        if (role.equals(CourseRoleType.EXPLORER))
            return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(person.getPersonId(), courseId).isPresent();
        else
            return keeperRepository.findKeeperByPersonIdAndCourseId(person.getPersonId(), courseId).isPresent();
    }

    public boolean hasAnyCoursesRole(List<Integer> courseIds, CourseRoleType role) {
        PeopleService.Person person = (PeopleService.Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (role.equals(CourseRoleType.EXPLORER)) {
            return courseIds.stream().allMatch(cId ->
                    explorerRepository.findExplorersByPersonIdAndGroupCourseIdIn(person.getPersonId(), courseIds)
                            .getExplorerWithCourseIdMapMap()
                            .containsKey(cId));
        } else {
            return courseIds.stream().allMatch(cId ->
                    keeperRepository.findKeepersByPersonIdAndGroupCourseIdIn(person.getPersonId(), courseIds)
                            .getKeeperWithCourseIdMapMap()
                            .containsKey(cId));
        }
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByRequestId(Integer requestId, CourseRoleType role) {
        return hasAnyCourseRole(
                courseRegistrationRequestRepository.findById(requestId)
                        .orElseThrow(() -> new RequestNotFoundException(requestId)).getCourseId(),
                role
        );
    }

    @Transactional(readOnly = true)
    public boolean hasAnyCourseRoleByRequestIds(List<Integer> requestIds, CourseRoleType role) {
        List<Integer> courseIds = courseRegistrationRequestRepository
                .findCourseRegistrationRequestsByRequestIdIn(requestIds)
                .stream()
                .map(CourseRegistrationRequest::getCourseId)
                .collect(Collectors.toList());
        return hasAnyCoursesRole(courseIds, role);
    }

    public boolean isPersonInRequest(Integer requestId) {
        return courseRegistrationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId))
                .getPersonId().equals(personService.getAuthenticatedPersonId());
    }

    public boolean isPersonKeepers(List<Integer> keeperIds) {
        PeopleService.Person person = personService.getAuthenticatedPerson();
        return keeperRepository.findKeepersByPersonId(person.getPersonId())
                .stream()
                .allMatch(k -> keeperIds.contains(k.getKeeperId()));
    }
}
