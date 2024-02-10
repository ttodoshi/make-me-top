package org.example.course.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.course.config.security.RoleService;
import org.example.course.dto.progress.CourseThemeCompletedDto;
import org.example.course.dto.theme.UpdateCourseThemeDto;
import org.example.course.enums.AuthenticationRoleType;
import org.example.course.enums.CourseRoleType;
import org.example.course.exception.course.CourseNotFoundException;
import org.example.course.exception.theme.CourseThemeAlreadyExistsException;
import org.example.course.exception.theme.CourseThemeNotFoundException;
import org.example.course.exception.theme.ThemeClosedException;
import org.example.course.repository.CourseRepository;
import org.example.course.repository.CourseThemeRepository;
import org.example.course.service.CourseProgressService;
import org.example.course.service.ExplorerService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseThemeValidatorService {
    private final CourseRepository courseRepository;
    private final CourseThemeRepository courseThemeRepository;

    private final ExplorerService explorerService;
    private final CourseProgressService courseProgressService;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public void validateGetThemeRequest(String authorizationHeader, Authentication authentication, Long courseThemeId) {
        if (!(roleService.hasAnyCourseRoleByThemeId(authorizationHeader, (Long) authentication.getPrincipal(), courseThemeId, CourseRoleType.EXPLORER) ||
                roleService.hasAnyCourseRoleByThemeId(authorizationHeader, (Long) authentication.getPrincipal(), courseThemeId, CourseRoleType.KEEPER))) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        if (roleService.hasAnyAuthenticationRole(authentication.getAuthorities(), AuthenticationRoleType.EXPLORER) &&
                !isThemeOpened(authorizationHeader, (Long) authentication.getPrincipal(), courseThemeId)) {
            throw new ThemeClosedException(courseThemeId);
        }
    }

    private boolean isThemeOpened(String authorizationHeader, Long authenticatedPersonId, Long themeId) {
        Long courseId = courseThemeRepository.findById(themeId)
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCourseId();

        List<CourseThemeCompletedDto> themesProgress = courseProgressService.getCourseProgress(
                authorizationHeader, explorerService.findExplorerByPersonIdAndGroup_CourseId(
                        authorizationHeader, authenticatedPersonId, courseId
                ).getExplorerId()
        ).getThemesWithProgress();

        Boolean themeCompleted = themesProgress
                .stream()
                .filter(t -> t.getCourseThemeId().equals(themeId))
                .findAny()
                .orElseThrow(() -> new CourseThemeNotFoundException(themeId))
                .getCompleted();
        return themeId.equals(getCurrentCourseThemeId(themesProgress)) || themeCompleted;
    }

    private Long getCurrentCourseThemeId(List<CourseThemeCompletedDto> themesProgress) {
        for (CourseThemeCompletedDto planet : themesProgress) {
            if (!planet.getCompleted())
                return planet.getCourseThemeId();
        }
        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
    }

    @Transactional(readOnly = true)
    public void validatePutRequest(Long themeId, UpdateCourseThemeDto theme) {
        if (!courseRepository.existsById(theme.getCourseId()))
            throw new CourseNotFoundException(theme.getCourseId());

        boolean themeTitleExists = courseThemeRepository
                .findCourseThemesByCourseIdOrderByCourseThemeNumber(
                        theme.getCourseId()
                ).stream()
                .anyMatch(t -> t.getTitle().equals(theme.getTitle()) && !t.getCourseThemeId().equals(themeId));
        if (themeTitleExists)
            throw new CourseThemeAlreadyExistsException(theme.getTitle());
    }
}
