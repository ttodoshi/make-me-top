package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.dto.homework.HomeworkUpdateRequest;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerGroupIsNotOnCourseException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotInGroupException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotForGroupException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.model.ExplorerGroup;
import org.example.model.Keeper;
import org.example.model.Person;
import org.example.model.homework.Homework;
import org.example.model.role.AuthenticationRoleType;
import org.example.repository.ExplorerGroupRepository;
import org.example.repository.ExplorerRepository;
import org.example.repository.KeeperRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.homework.HomeworkRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeworkValidatorService {
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRepository homeworkRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    private final RoleService roleService;

    public void validateGetRequest(Integer groupId) {
        ExplorerGroup explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        final Integer personId = getAuthenticatedPersonId();
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            isExplorerInGroup(personId, explorerGroup);
        } else {
            isKeeperForGroup(personId, explorerGroup);
        }
    }

    private void isExplorerInGroup(Integer personId, ExplorerGroup explorerGroup) {
        if (explorerRepository.findExplorerByPersonIdAndGroupId(personId, explorerGroup.getGroupId()).isEmpty())
            throw new ExplorerNotInGroupException();
    }

    private void isKeeperForGroup(Integer personId, ExplorerGroup explorerGroup) {
        Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(personId, explorerGroup.getCourseId())
                .orElseThrow(KeeperNotFoundException::new);
        if (!explorerGroup.getKeeperId().equals(keeper.getKeeperId()))
            throw new KeeperNotForGroupException();
    }

    private Integer getAuthenticatedPersonId() {
        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticatedPerson.getPersonId();
    }

    public void validatePostRequest(Integer themeId, Integer groupId) {
        ExplorerGroup explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        Integer courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();
        if (!explorerGroup.getCourseId().equals(courseId))
            throw new ExplorerGroupIsNotOnCourseException(groupId, courseId);
        isKeeperForGroup(getAuthenticatedPersonId(), explorerGroup);
    }

    public void validatePutRequest(HomeworkUpdateRequest homeworkUpdateRequest) {
        ExplorerGroup explorerGroup = explorerGroupRepository.findById(homeworkUpdateRequest.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(homeworkUpdateRequest.getGroupId()));
        Integer courseId = courseThemeRepository.findById(homeworkUpdateRequest.getCourseThemeId())
                .orElseThrow(() -> new CourseThemeNotFoundException(homeworkUpdateRequest.getCourseThemeId()))
                .getCourseId();
        if (!explorerGroup.getCourseId().equals(courseId))
            throw new ExplorerGroupIsNotOnCourseException(homeworkUpdateRequest.getGroupId(), courseId);
        isKeeperForGroup(getAuthenticatedPersonId(), explorerGroup);
    }

    public void validateDeleteRequest(Integer homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        ExplorerGroup explorerGroup = explorerGroupRepository.findById(homework.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(homework.getGroupId()));
        isKeeperForGroup(getAuthenticatedPersonId(), explorerGroup);
    }
}
