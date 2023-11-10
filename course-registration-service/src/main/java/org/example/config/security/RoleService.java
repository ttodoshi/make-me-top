package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.grpc.PeopleService;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.service.PersonService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return requestIds.stream().allMatch(rId -> hasAnyCourseRoleByRequestId(rId, role));
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

    public boolean isPerson(Integer personId) {
        return personService.getAuthenticatedPersonId().equals(personId);
    }
}
