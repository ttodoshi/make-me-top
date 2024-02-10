package org.example.person.service.implementations.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.config.security.RoleService;
import org.example.person.enums.AuthenticationRoleType;
import org.example.person.exception.course.CourseNotFoundException;
import org.example.person.exception.explorer.ExplorerNotFoundException;
import org.example.person.exception.person.PersonNotFoundException;
import org.example.person.exception.progress.ExplorerAlreadyHasMarkException;
import org.example.person.repository.ExplorerRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.progress.CourseMarkService;
import org.example.person.service.api.validator.ExplorerValidatorService;
import org.example.person.service.implementations.PersonService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerValidatorServiceImpl implements ExplorerValidatorService {
    private final CourseService courseService;
    private final ExplorerRepository explorerRepository;
    private final PersonService personService;
    private final CourseMarkService courseMarkService;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public void validateGetExplorersByPersonIdRequest(Long personId) {
        if (!personService.personExistsById(personId)) {
            log.warn("person by id {} not found", personId);
            throw new PersonNotFoundException(personId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateGetExplorersByCourseIdRequest(String authorizationHeader, Long courseId) {
        if (!courseService.existsById(authorizationHeader, courseId)) {
            log.warn("course by id {} not found", courseId);
            throw new CourseNotFoundException(courseId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateDeleteExplorerByIdRequest(String authorizationHeader, Authentication authentication, Long explorerId) {
        boolean hasAccess = roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.EXPLORER) &&
                roleService.isPersonExplorer((Long) authentication.getPrincipal(), explorerId) ||
                roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.KEEPER) &&
                        roleService.isKeeperForExplorer((Long) authentication.getPrincipal(), explorerId);
        if (!hasAccess) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        if (!explorerRepository.existsById(explorerId)) {
            log.warn("explorer by id {} not found", explorerId);
            throw new ExplorerNotFoundException(explorerId);
        }
        if (courseMarkService.findById(authorizationHeader, explorerId).isPresent()) {
            log.warn("course mark already exists for explorer {}", explorerId);
            throw new ExplorerAlreadyHasMarkException();
        }
    }
}
