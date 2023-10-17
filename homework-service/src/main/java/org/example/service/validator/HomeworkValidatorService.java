package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.config.security.RoleService;
import org.example.config.security.role.AuthenticationRoleType;
import org.example.dto.explorer.ExplorerGroupDto;
import org.example.dto.homework.UpdateHomeworkDto;
import org.example.dto.keeper.KeeperDto;
import org.example.dto.progress.CourseThemeCompletedDto;
import org.example.exception.classes.coursethemeEX.CourseThemeNotFoundException;
import org.example.exception.classes.coursethemeEX.ThemeClosedException;
import org.example.exception.classes.coursethemeEX.ThemeFromDifferentCourseException;
import org.example.exception.classes.explorerEX.ExplorerGroupIsNotOnCourseException;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.exception.classes.explorerEX.ExplorerNotInGroupException;
import org.example.exception.classes.homeworkEX.HomeworkNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotForGroupException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.model.Homework;
import org.example.repository.*;
import org.example.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HomeworkValidatorService {
    private final CourseThemeRepository courseThemeRepository;
    private final HomeworkRepository homeworkRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final CourseProgressRepository courseProgressRepository;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public void validateGetRequest(Integer themeId, Integer groupId) {
        ExplorerGroupDto explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        Integer courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();
        if (!explorerGroup.getCourseId().equals(courseId))
            throw new ThemeFromDifferentCourseException(themeId, groupId);
        final Integer personId = personService.getAuthenticatedPersonId();
        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            isExplorerInGroup(personId, explorerGroup);
            if (!isThemeOpened(
                    explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, explorerGroup.getCourseId())
                            .orElseThrow(ExplorerNotFoundException::new)
                            .getExplorerId(),
                    themeId)
            )
                throw new ThemeClosedException(themeId);
        } else {
            isKeeperForGroup(personId, explorerGroup);
        }
    }

    private boolean isThemeOpened(Integer explorerId, Integer themeId) {
        List<CourseThemeCompletedDto> themesProgress = courseProgressRepository
                .getCourseProgress(explorerId)
                .getThemesWithProgress();
        Optional<CourseThemeCompletedDto> themeCompletion = themesProgress
                .stream()
                .filter(t -> t.getCourseThemeId().equals(themeId))
                .findAny();
        Boolean themeCompleted = themeCompletion.orElseThrow(
                () -> new CourseThemeNotFoundException(themeId)
        ).getCompleted();
        return themeId.equals(getCurrentCourseThemeId(themesProgress)) || themeCompleted;
    }

    private Integer getCurrentCourseThemeId(List<CourseThemeCompletedDto> themesProgress) {
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private void isExplorerInGroup(Integer personId, ExplorerGroupDto explorerGroup) {
        if (explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, explorerGroup.getCourseId()).isEmpty())
            throw new ExplorerNotInGroupException();
    }

    private void isKeeperForGroup(Integer personId, ExplorerGroupDto explorerGroup) {
        KeeperDto keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(personId, explorerGroup.getCourseId())
                .orElseThrow(KeeperNotFoundException::new);
        if (!explorerGroup.getKeeperId().equals(keeper.getKeeperId()))
            throw new KeeperNotForGroupException();
    }

    @Transactional(readOnly = true)
    public void validateGetCompletedRequest(Integer themeId, Integer groupId, Integer explorerId) {
        if (!explorerRepository.existsById(explorerId))
            throw new ExplorerNotFoundException(explorerId);
        validateGetRequest(themeId, groupId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Integer themeId, Integer groupId) {
        ExplorerGroupDto explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        Integer courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();
        if (!explorerGroup.getCourseId().equals(courseId))
            throw new ExplorerGroupIsNotOnCourseException(groupId, courseId);
        isKeeperForGroup(personService.getAuthenticatedPersonId(), explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(UpdateHomeworkDto updateHomeworkDto) {
        ExplorerGroupDto explorerGroup = explorerGroupRepository.findById(updateHomeworkDto.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(updateHomeworkDto.getGroupId()));
        Integer courseId = courseThemeRepository.findById(updateHomeworkDto.getCourseThemeId())
                .orElseThrow(() -> new CourseThemeNotFoundException(updateHomeworkDto.getCourseThemeId()))
                .getCourseId();
        if (!explorerGroup.getCourseId().equals(courseId))
            throw new ExplorerGroupIsNotOnCourseException(updateHomeworkDto.getGroupId(), courseId);
        isKeeperForGroup(personService.getAuthenticatedPersonId(), explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Integer homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        ExplorerGroupDto explorerGroup = explorerGroupRepository.findById(homework.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(homework.getGroupId()));
        isKeeperForGroup(personService.getAuthenticatedPersonId(), explorerGroup);
    }
}
