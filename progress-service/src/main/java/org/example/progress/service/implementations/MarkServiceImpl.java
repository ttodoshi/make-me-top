package org.example.progress.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorersService;
import org.example.progress.config.security.RoleService;
import org.example.progress.dto.explorer.ExplorerBasicInfoDto;
import org.example.progress.dto.group.CurrentKeeperGroupDto;
import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.dto.mark.CourseMarkDto;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.enums.CourseRoleType;
import org.example.progress.exception.mark.CourseMarkNotFoundException;
import org.example.progress.exception.planet.PlanetNotFoundException;
import org.example.progress.model.CourseMark;
import org.example.progress.model.CourseThemeCompletion;
import org.example.progress.repository.CourseMarkRepository;
import org.example.progress.repository.CourseThemeCompletionRepository;
import org.example.progress.service.*;
import org.example.progress.service.validator.MarkValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkServiceImpl implements MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;

    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;
    private final HomeworkService homeworkService;
    private final PlanetService planetService;
    private final RoleService roleService;

    private final MarkValidatorService markValidatorService;

    private final ModelMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public CourseMarkDto findCourseMarkById(Long explorerId) {
        return courseMarkRepository.findById(explorerId)
                .map(m -> mapper.map(m, CourseMarkDto.class))
                .orElseThrow(() -> new CourseMarkNotFoundException(explorerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> findThemesMarks(String authorizationHeader, Long authenticatedPersonId, Long courseId) {
        markValidatorService.validateThemesMarksRequest(authorizationHeader, courseId);

        ExplorersService.Explorer explorer = explorerService.findExplorerByPersonIdAndGroup_CourseId(
                authorizationHeader, authenticatedPersonId, courseId
        );

        return courseThemeCompletionRepository
                .findCourseThemeCompletionByExplorerIdAndCourseThemeIdIn(
                        explorer.getExplorerId(),
                        planetService.findPlanetsBySystemId(authorizationHeader, courseId)
                                .stream()
                                .map(PlanetDto::getPlanetId)
                                .collect(Collectors.toList())
                ).stream()
                .collect(Collectors.toMap(
                        CourseThemeCompletion::getCourseThemeId,
                        CourseThemeCompletion::getMark
                ));
    }

    @Override
    @Transactional
    public Long setCourseMark(String authorizationHeader, Long authenticatedPersonId, MarkDto mark) {
        markValidatorService.validateCourseMarkRequest(authorizationHeader, authenticatedPersonId, mark);
        return courseMarkRepository.save(
                new CourseMark(mark.getExplorerId(), mark.getValue())
        ).getExplorerId();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getThemesWaitingForExplorersMark(String authorizationHeader) {
        Optional<CurrentKeeperGroupDto> currentGroup = explorerGroupService.getCurrentGroup(authorizationHeader);
        if (currentGroup.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> themeIds = new HashSet<>();

        List<PlanetDto> planets = planetService.findPlanetsBySystemId(
                authorizationHeader, currentGroup.get().getCourseId()
        );

        Map<Long, List<HomeworkDto>> homeworks = homeworkService.findHomeworksByCourseThemeIdInAndGroupId(
                authorizationHeader,
                planets.stream()
                        .map(PlanetDto::getPlanetId)
                        .collect(Collectors.toList()),
                currentGroup.get().getGroupId()
        );

        Map<Long, Map<Long, List<HomeworkDto>>> completedHomeworks = homeworkService.findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(
                authorizationHeader,
                planets.stream()
                        .map(PlanetDto::getPlanetId)
                        .collect(Collectors.toList()),
                currentGroup.get().getGroupId(),
                currentGroup.get().getExplorers()
                        .stream()
                        .map(ExplorerBasicInfoDto::getExplorerId)
                        .collect(Collectors.toList())
        );

        for (ExplorerBasicInfoDto explorer : currentGroup.get().getExplorers()) {
            for (PlanetDto planet : planets) {
                if (!courseThemeCompletionRepository.existsByExplorerIdAndCourseThemeId(explorer.getExplorerId(), planet.getPlanetId())) {
                    if (homeworks.getOrDefault(planet.getPlanetId(), Collections.emptyList()).size() == completedHomeworks.get(planet.getPlanetId()).getOrDefault(explorer.getExplorerId(), Collections.emptyList()).size()) {
                        themeIds.add(planet.getPlanetId());
                    }
                    break;
                }
            }
        }

        return themeIds;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExplorerBasicInfoDto> getExplorersWaitingForThemeMark(String authorizationHeader, Long authenticatedPersonId, Long themeId) {
        if (!roleService.hasAnyCourseRoleByThemeId(authorizationHeader, authenticatedPersonId, themeId, CourseRoleType.KEEPER)) {
            throw new AccessDeniedException("Вам закрыт доступ к данной функциональности бортового компьютера");
        }
        Optional<CurrentKeeperGroupDto> currentGroup = explorerGroupService.getCurrentGroup(authorizationHeader);

        return currentGroup.map(group -> {
            List<PlanetDto> planets = planetService.findPlanetsBySystemId(
                    authorizationHeader, group.getCourseId()
            );
            if (planets.stream().noneMatch(p -> p.getPlanetId().equals(themeId))) {
                return Collections.<ExplorerBasicInfoDto>emptyList();
            }

            List<HomeworkDto> homeworks = homeworkService.findHomeworksByCourseThemeIdAndGroupId(
                    authorizationHeader, themeId, group.getGroupId()
            );
            Map<Long, List<HomeworkDto>> completedExplorersHomeworks = homeworkService
                    .findAllCompletedByCourseThemeIdAndGroupIdForExplorers(
                            authorizationHeader, themeId, group.getGroupId(),
                            group.getExplorers()
                                    .stream()
                                    .map(ExplorerBasicInfoDto::getExplorerId)
                                    .collect(Collectors.toList())
                    );

            return group.getExplorers().stream()
                    .filter(e -> homeworks.size() == completedExplorersHomeworks.getOrDefault(e.getExplorerId(), Collections.emptyList()).size() &&
                            !courseThemeCompletionRepository.existsByExplorerIdAndCourseThemeId(e.getExplorerId(), themeId) &&
                            (isFirstPlanet(themeId, planets) || previousThemeMarkExists(themeId, planets, e.getExplorerId()))
                    ).collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    private boolean isFirstPlanet(Long themeId, List<PlanetDto> planets) {
        if (!planets.isEmpty()) {
            return themeId.equals(planets.get(0).getPlanetId());
        }
        return false;
    }

    private boolean previousThemeMarkExists(Long themeId, List<PlanetDto> planets, Long explorerId) {
        return courseThemeCompletionRepository.existsByExplorerIdAndCourseThemeId(
                explorerId, getPreviousPlanetId(themeId, planets)
        );
    }

    private Long getPreviousPlanetId(Long planetId, List<PlanetDto> planets) {
        if (planets.isEmpty() || planets.size() == 1) {
            return planetId;
        }
        for (int i = 0; i < planets.size(); i++) {
            if (planets.get(i).getPlanetId().equals(planetId)) {
                return i == 0 ? planets.get(0).getPlanetId() : planets.get(i - 1).getPlanetId();
            }
        }
        throw new PlanetNotFoundException(planetId);
    }

    @Override
    @Transactional
    public Long setThemeMark(String authorizationHeader, Long authenticatedPersonId, Long themeId, MarkDto mark) {
        markValidatorService.validateThemeMarkRequest(authorizationHeader, authenticatedPersonId, themeId, mark);
        return courseThemeCompletionRepository.save(
                new CourseThemeCompletion(mark.getExplorerId(), themeId, mark.getValue())
        ).getCourseThemeCompletionId();
    }

    @KafkaListener(topics = "deleteExplorersProgressTopic", containerFactory = "deleteExplorersProgressKafkaListenerContainerFactory")
    @Transactional
    public void deleteExplorersProgressByThemeId(Long themeId) {
        courseThemeCompletionRepository.deleteCourseThemeCompletionsByCourseThemeId(themeId);
    }

    @KafkaListener(topics = "deleteProgressAndMarkTopic", containerFactory = "deleteProgressAndMarkKafkaListenerContainerFactory")
    @Transactional
    public void deleteProgressAndMarkByExplorerId(Long explorerId) {
        courseThemeCompletionRepository.deleteCourseThemeCompletionsByExplorerId(explorerId);
        if (courseMarkRepository.existsById(explorerId))
            courseMarkRepository.deleteById(explorerId);
    }
}