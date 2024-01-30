package org.example.progress.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorersService;
import org.example.progress.dto.explorer.ExplorerBasicInfoDto;
import org.example.progress.dto.group.CurrentKeeperGroupDto;
import org.example.progress.dto.homework.HomeworkDto;
import org.example.progress.dto.mark.CourseMarkDto;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.dto.planet.PlanetDto;
import org.example.progress.exception.classes.explorer.ExplorerNotFoundException;
import org.example.progress.exception.classes.mark.CourseMarkNotFoundException;
import org.example.progress.exception.classes.planet.PlanetNotFoundException;
import org.example.progress.model.CourseMark;
import org.example.progress.model.CourseThemeCompletion;
import org.example.progress.repository.*;
import org.example.progress.service.validator.MarkValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;
    private final HomeworkRepository homeworkRepository;
    private final PlanetRepository planetRepository;

    private final PersonService personService;
    private final MarkValidatorService markValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public CourseMarkDto findCourseMarkById(Long explorerId) {
        return courseMarkRepository.findById(explorerId)
                .map(m -> mapper.map(m, CourseMarkDto.class))
                .orElseThrow(() -> new CourseMarkNotFoundException(explorerId));
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> findThemesMarks(Long courseId) {
        markValidatorService.validateThemesMarksRequest(courseId);

        Long authenticatedPersonId = personService.getAuthenticatedPersonId();
        ExplorersService.Explorer explorer = explorerRepository.findExplorerByPersonIdAndGroup_CourseId(
                authenticatedPersonId,
                courseId
        ).orElseThrow(ExplorerNotFoundException::new);

        return courseThemeCompletionRepository
                .findCourseThemeCompletionByExplorerIdAndCourseThemeIdIn(
                        explorer.getExplorerId(),
                        planetRepository.findPlanetsBySystemId(courseId)
                                .stream()
                                .map(PlanetDto::getPlanetId)
                                .collect(Collectors.toList())
                ).stream()
                .collect(Collectors.toMap(
                        CourseThemeCompletion::getCourseThemeId,
                        CourseThemeCompletion::getMark
                ));
    }

    @Transactional
    public Long setCourseMark(MarkDto mark) {
        markValidatorService.validateCourseMarkRequest(mark);
        return courseMarkRepository.save(
                new CourseMark(mark.getExplorerId(), mark.getValue())
        ).getExplorerId();
    }

    @Transactional(readOnly = true)
    public Set<Long> getThemesWaitingForExplorersMark() {
        Optional<CurrentKeeperGroupDto> currentGroup = explorerGroupRepository.getCurrentGroup();
        if (currentGroup.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> themeIds = new HashSet<>();

        List<PlanetDto> planets = planetRepository.findPlanetsBySystemId(
                currentGroup.get().getCourseId()
        );

        Map<Long, List<HomeworkDto>> homeworks = homeworkRepository.findHomeworksByCourseThemeIdInAndGroupId(
                planets.stream()
                        .map(PlanetDto::getPlanetId)
                        .collect(Collectors.toList()),
                currentGroup.get().getGroupId()
        );

        Map<Long, Map<Long, List<HomeworkDto>>> completedHomeworks = homeworkRepository.findAllCompletedByCourseThemeIdInAndGroupIdForExplorers(
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

    @Transactional(readOnly = true)
    public List<ExplorerBasicInfoDto> getExplorersWaitingForThemeMark(Long themeId) {
        Optional<CurrentKeeperGroupDto> currentGroup = explorerGroupRepository.getCurrentGroup();
        if (currentGroup.isEmpty()) {
            return Collections.emptyList();
        }

        List<HomeworkDto> homeworks = homeworkRepository.findHomeworksByCourseThemeIdAndGroupId(themeId, currentGroup.get().getGroupId());
        Map<Long, List<HomeworkDto>> completedExplorersHomeworks = homeworkRepository.findAllCompletedByCourseThemeIdAndGroupIdForExplorers(
                themeId, currentGroup.get().getGroupId(), currentGroup.get().getExplorers().stream().map(ExplorerBasicInfoDto::getExplorerId).collect(Collectors.toList())
        );

        List<PlanetDto> planets = planetRepository.findPlanetsBySystemId(
                currentGroup.get().getCourseId()
        );

        return currentGroup.get()
                .getExplorers()
                .stream()
                .filter(e -> homeworks.size() == completedExplorersHomeworks.get(e.getExplorerId()).size() &&
                        !courseThemeCompletionRepository.existsByExplorerIdAndCourseThemeId(e.getExplorerId(), themeId) &&
                        (isFirstPlanet(themeId, planets) || previousThemeMarkExists(themeId, planets, e.getExplorerId()))
                ).collect(Collectors.toList());
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

    @Transactional
    public Long setThemeMark(Long themeId, MarkDto mark) {
        markValidatorService.validateThemeMarkRequest(themeId, mark);
        return courseThemeCompletionRepository.save(
                new CourseThemeCompletion(mark.getExplorerId(), themeId, mark.getValue())
        ).getCourseThemeCompletionId();
    }

    @KafkaListener(topics = "deleteExplorersProgressTopic", containerFactory = "deleteExplorersProgressKafkaListenerContainerFactory")
    @Transactional
    public void deleteExplorersProgressByThemeId(Long themeId) {
        courseThemeCompletionRepository
                .deleteCourseThemeCompletionsByCourseThemeId(themeId);
    }

    @KafkaListener(topics = "deleteProgressAndMarkTopic", containerFactory = "deleteProgressAndMarkKafkaListenerContainerFactory")
    @Transactional
    public void deleteProgressAndMarkByExplorerId(Long explorerId) {
        courseThemeCompletionRepository
                .deleteCourseThemeCompletionsByExplorerId(explorerId);
        if (courseMarkRepository.existsById(explorerId))
            courseMarkRepository.deleteById(explorerId);
    }
}