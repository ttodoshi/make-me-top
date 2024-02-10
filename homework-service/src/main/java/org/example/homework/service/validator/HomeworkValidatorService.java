package org.example.homework.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.KeepersService;
import org.example.homework.config.security.RoleService;
import org.example.homework.dto.homework.UpdateHomeworkDto;
import org.example.homework.dto.planet.PlanetDto;
import org.example.homework.dto.progress.CourseThemeCompletedDto;
import org.example.homework.enums.AuthenticationRoleType;
import org.example.homework.exception.explorer.ExplorerGroupIsNotOnCourseException;
import org.example.homework.exception.explorer.ExplorerNotFoundException;
import org.example.homework.exception.explorer.ExplorerNotInGroupException;
import org.example.homework.exception.homework.HomeworkNotFoundException;
import org.example.homework.exception.keeper.KeeperNotForGroupException;
import org.example.homework.exception.theme.CourseThemeNotFoundException;
import org.example.homework.exception.theme.ThemeClosedException;
import org.example.homework.exception.theme.ThemeFromDifferentCourseException;
import org.example.homework.model.Homework;
import org.example.homework.repository.HomeworkRepository;
import org.example.homework.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeworkValidatorService {
    private final HomeworkRepository homeworkRepository;
    private final ExplorerGroupService explorerGroupService;
    private final ExplorerService explorerService;
    private final KeeperService keeperService;
    private final PlanetService planetService;

    private final CourseProgressService courseProgressService;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public void validateGetRequest(String authorizationHeader, Authentication authentication, Long themeId, Long groupId) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(authorizationHeader, groupId);
        Long courseId = planetService.findById(authorizationHeader, themeId).getSystemId();

        if (!courseId.equals(explorerGroup.getCourseId())) {
            log.warn("the theme {} does not apply to the course that the group {} is studying", themeId, groupId);
            throw new ThemeFromDifferentCourseException(themeId, groupId);
        }

        if (roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.EXPLORER)) {
            isExplorerInGroup(authorizationHeader, (Long) authentication.getPrincipal(), explorerGroup);

            if (!isThemeOpened(
                    authorizationHeader,
                    explorerService.findExplorerByPersonIdAndGroup_CourseId(
                            authorizationHeader, (Long) authentication.getPrincipal(), explorerGroup.getCourseId()
                    ).getExplorerId(),
                    themeId)) {
                log.warn("theme {} closed for explorer", themeId);
                throw new ThemeClosedException(themeId);
            }
        } else {
            isKeeperForGroup(authorizationHeader, (Long) authentication.getPrincipal(), explorerGroup);
        }
    }

    @Transactional(readOnly = true)
    public void validateGetByCourseThemeIdInRequest(String authorizationHeader, Long authenticatedPersonId, Long groupId) {
        isKeeperForGroup(
                authorizationHeader, authenticatedPersonId, explorerGroupService.findById(authorizationHeader, groupId)
        );
    }

    private boolean isThemeOpened(String authorizationHeader, Long explorerId, Long themeId) {
        List<CourseThemeCompletedDto> themesProgress = courseProgressService
                .getCourseProgress(authorizationHeader, explorerId)
                .getThemesWithProgress();
        Optional<CourseThemeCompletedDto> themeCompletion = themesProgress
                .stream()
                .filter(t -> t.getCourseThemeId().equals(themeId))
                .findAny();

        Boolean themeCompleted = themeCompletion.orElseThrow(
                () -> {
                    log.warn("theme by id {} not found", themeId);
                    return new CourseThemeNotFoundException(themeId);
                }
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

    private void isExplorerInGroup(String authorizationHeader, Long personId, ExplorerGroupsService.ExplorerGroup explorerGroup) {
        if (!explorerService.existsExplorerByPersonIdAndGroup_CourseId(authorizationHeader, personId, explorerGroup.getCourseId())) {
            log.warn("person {} is not in group {}", personId, explorerGroup.getGroupId());
            throw new ExplorerNotInGroupException();
        }
    }

    private void isKeeperForGroup(String authorizationHeader, Long personId, ExplorerGroupsService.ExplorerGroup explorerGroup) {
        KeepersService.Keeper keeper = keeperService.findKeeperByPersonIdAndCourseId(
                authorizationHeader, personId, explorerGroup.getCourseId()
        );
        if (!(explorerGroup.getKeeperId() == keeper.getKeeperId())) {
            log.warn("person {} is not keeper for group {}", personId, explorerGroup.getGroupId());
            throw new KeeperNotForGroupException();
        }
    }

    @Transactional(readOnly = true)
    public void validateGetCompletedRequest(String authorizationHeader, Long authenticatedPersonId, List<Long> themeIds, Long groupId, List<Long> explorerIds) {
        if (explorerService.findExplorersByExplorerIdIn(authorizationHeader, explorerIds).size() != explorerIds.size()) {
            log.warn("all explorers by explorer ids not found");
            throw new ExplorerNotFoundException();
        }
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(authorizationHeader, groupId);

        for (PlanetDto planet : planetService.findPlanetsByPlanetIdIn(authorizationHeader, themeIds).values()) {
            if (!planet.getSystemId().equals(explorerGroup.getCourseId())) {
                log.warn("the theme {} does not apply to the course that the group {} is studying", planet.getPlanetId(), groupId);
                throw new ThemeFromDifferentCourseException(planet.getPlanetId(), groupId);
            }
        }
        isKeeperForGroup(authorizationHeader, authenticatedPersonId, explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validatePostRequest(String authorizationHeader, Long authenticatedPersonId, Long themeId, Long groupId) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(authorizationHeader, groupId);
        Long courseId = planetService.findById(authorizationHeader, themeId).getSystemId();

        if (!courseId.equals(explorerGroup.getCourseId())) {
            log.warn("group {} is not on course {}", groupId, courseId);
            throw new ExplorerGroupIsNotOnCourseException(groupId, courseId);
        }
        isKeeperForGroup(authorizationHeader, authenticatedPersonId, explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(String authorizationHeader, Long authenticatedPersonId, UpdateHomeworkDto updateHomeworkDto) {
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(
                authorizationHeader, updateHomeworkDto.getGroupId()
        );
        Long courseId = planetService.findById(authorizationHeader, updateHomeworkDto.getCourseThemeId()).getSystemId();

        if (!courseId.equals(explorerGroup.getCourseId())) {
            log.warn("group {} is not on course {}", updateHomeworkDto.getGroupId(), courseId);
            throw new ExplorerGroupIsNotOnCourseException(updateHomeworkDto.getGroupId(), courseId);
        }
        isKeeperForGroup(authorizationHeader, authenticatedPersonId, explorerGroup);
    }

    @Transactional(readOnly = true)
    public void validateDeleteRequest(String authorizationHeader, Long authenticatedPersonId, Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> {
                    log.warn("homework by id {} not found", homeworkId);
                    return new HomeworkNotFoundException(homeworkId);
                });
        ExplorerGroupsService.ExplorerGroup explorerGroup = explorerGroupService.findById(authorizationHeader, homework.getGroupId());

        isKeeperForGroup(authorizationHeader, authenticatedPersonId, explorerGroup);
    }
}
