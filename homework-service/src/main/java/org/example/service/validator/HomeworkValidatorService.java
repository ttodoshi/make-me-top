package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.dto.courseprogress.CourseThemeCompletionDTO;
import org.example.dto.homework.HomeworkUpdateRequest;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.coursethemeEX.ThemeClosedException;
import org.example.exception.classes.coursethemeEX.ThemeFromDifferentCourseException;
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
import org.example.repository.course.CourseRepository;
import org.example.repository.course.CourseThemeRepository;
import org.example.repository.custom.CourseProgressRepository;
import org.example.repository.homework.HomeworkRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HomeworkValidatorService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRepository homeworkRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final CourseProgressRepository courseProgressRepository;

    private final RoleService roleService;

    @Transactional(readOnly = true)
    public void validateGetRequest(Integer themeId, Integer groupId) {
        ExplorerGroup explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        Integer courseId = courseRepository.getCourseIdByThemeId(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId));
        if (!explorerGroup.getCourseId().equals(courseId))
            throw new ThemeFromDifferentCourseException(themeId, groupId);
        final Integer personId = getAuthenticatedPersonId();
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            isExplorerInGroup(personId, explorerGroup);
            if (!isThemeOpened(courseId, themeId))
                throw new ThemeClosedException(themeId);
        } else {
            isKeeperForGroup(personId, explorerGroup);
        }
    }

    private boolean isThemeOpened(Integer courseId, Integer themeId) {
        List<CourseThemeCompletionDTO> themesProgress = courseProgressRepository
                .getCourseProgress(courseId)
                .getThemesWithProgress();
        Optional<CourseThemeCompletionDTO> themeCompletion = themesProgress
                .stream()
                .filter(t -> t.getCourseThemeId().equals(themeId))
                .findAny();
        Boolean themeCompleted = themeCompletion.orElseThrow(
                () -> new CourseThemeNotFoundException(themeId)
        ).getCompleted();
        return themeId.equals(getCurrentCourseThemeId(themesProgress)) || themeCompleted;
    }

    private Integer getCurrentCourseThemeId(List<CourseThemeCompletionDTO> themesProgress) {
        for (CourseThemeCompletionDTO planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
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
