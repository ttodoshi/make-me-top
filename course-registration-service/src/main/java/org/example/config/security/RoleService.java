package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.config.security.role.CourseRoleType;
import org.example.dto.PersonDto;
import org.example.exception.classes.requestEX.RequestNotFoundException;
import org.example.repository.CourseRegistrationRequestRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
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
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return courseRegistrationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(requestId))
                .getPersonId().equals(person.getPersonId());
    }

    public boolean isPersonKeepers(List<Integer> keeperIds) {
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return keeperRepository.findKeepersByPersonId(person.getPersonId())
                .stream()
                .allMatch(k -> keeperIds.contains(k.getKeeperId()));
    }

    public boolean isPerson(Integer personId) {
        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return person.getPersonId().equals(personId);
    }
}
