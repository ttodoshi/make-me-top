package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.KeepersService;
import org.example.homework.config.security.RoleService;
import org.example.homework.dto.homework.UpdateHomeworkDto;
import org.example.homework.dto.progress.CourseThemeCompletedDto;
import org.example.homework.enums.AuthenticationRoleType;
import org.example.homework.exception.classes.explorer.ExplorerGroupIsNotOnCourseException;
import org.example.homework.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.homework.exception.classes.explorer.ExplorerNotFoundException;
import org.example.homework.exception.classes.explorer.ExplorerNotInGroupException;
import org.example.homework.exception.classes.homework.HomeworkNotFoundException;
import org.example.homework.exception.classes.keeper.KeeperNotForGroupException;
import org.example.homework.exception.classes.keeper.KeeperNotFoundException;
import org.example.homework.exception.classes.planet.PlanetNotFoundException;
import org.example.homework.exception.classes.theme.CourseThemeNotFoundException;
import org.example.homework.exception.classes.theme.ThemeClosedException;
import org.example.homework.exception.classes.theme.ThemeFromDifferentCourseException;
import org.example.homework.model.Homework;
import org.example.homework.repository.*;
import org.example.homework.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HomeworkValidatorService {
    private final HomeworkRepository homeworkRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final ExplorerRepository explorerRepository;
    private final KeeperRepository keeperRepository;
    private final PlanetRepository planetRepository;

    private final PersonService personService;
    private final CourseProgressRepository courseProgressRepository;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public void validateGetRequest(Long themeId, Long groupId) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        Long courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();

        if (!courseId.equals(explorerGroup.getCourseId()))
            throw new ThemeFromDifferentCourseException(themeId, groupId);

        final Long personId = personService.getAuthenticatedPersonId();

        if (roleService.hasAnyAuthenticationRole(AuthenticationRoleType.EXPLORER)) {
            isExplorerInGroup(personId, explorerGroup);

            if (!isThemeOpened(
                    explorerRepository
                            .findExplorerByPersonIdAndGroup_CourseId(personId, explorerGroup.getCourseId())
                            .orElseThrow(ExplorerNotFoundException::new)
                            .getExplorerId(),
                    themeId)) {
                throw new ThemeClosedException(themeId);
            }
        } else {
            isKeeperForGroup(personId, explorerGroup);
        }
    }

    private boolean isThemeOpened(Long explorerId, Long themeId) {
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

    private Long getCurrentCourseThemeId(List<CourseThemeCompletedDto> themesProgress) {
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    private void isExplorerInGroup(Long personId, ExplorerGroupsService.ExplorerGroup explorerGroup) {
        if (explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, explorerGroup.getCourseId()).isEmpty())
            throw new ExplorerNotInGroupException();
    }

    private void isKeeperForGroup(Long personId, ExplorerGroupsService.ExplorerGroup explorerGroup) {
        KeepersService.Keeper keeper = keeperRepository
                .findKeeperByPersonIdAndCourseId(personId, explorerGroup.getCourseId())
                .orElseThrow(KeeperNotFoundException::new);

        if (!(explorerGroup.getKeeperId() == keeper.getKeeperId()))
            throw new KeeperNotForGroupException();
    }

    @Transactional(readOnly = true)
    public void validateGetCompletedRequest(Long themeId, Long groupId, Long explorerId) {
        if (!explorerRepository.existsById(explorerId))
            throw new ExplorerNotFoundException(explorerId);
        validateGetRequest(themeId, groupId);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(Long themeId, Long groupId) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
        Long courseId = planetRepository.findById(themeId)
                .orElseThrow(() -> new PlanetNotFoundException(themeId))
                .getSystemId();

        if (!courseId.equals(explorerGroup.getCourseId()))
            throw new ExplorerGroupIsNotOnCourseException(groupId, courseId);
        isKeeperForGroup(personService.getAuthenticatedPersonId(), explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(UpdateHomeworkDto updateHomeworkDto) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(updateHomeworkDto.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(updateHomeworkDto.getGroupId()));
        Long courseId = planetRepository.findById(updateHomeworkDto.getCourseThemeId())
                .orElseThrow(() -> new PlanetNotFoundException(updateHomeworkDto.getCourseThemeId()))
                .getSystemId();

        if (!courseId.equals(explorerGroup.getCourseId()))
            throw new ExplorerGroupIsNotOnCourseException(updateHomeworkDto.getGroupId(), courseId);
        isKeeperForGroup(personService.getAuthenticatedPersonId(), explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new HomeworkNotFoundException(homeworkId));
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupRepository.findById(homework.getGroupId())
                .orElseThrow(() -> new ExplorerGroupNotFoundException(homework.getGroupId()));

        isKeeperForGroup(personService.getAuthenticatedPersonId(), explorerGroup);
    }
}
