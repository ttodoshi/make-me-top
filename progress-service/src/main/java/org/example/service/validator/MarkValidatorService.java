package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.mark.MarkDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarkValidatorService {
    // TODO
//    private final ExplorerRepository explorerRepository;
//    private final ExplorerGroupRepository explorerGroupRepository;
//    private final KeeperRepository keeperRepository;
//    private final CourseRepository courseRepository;
//    private final CourseThemeRepository courseThemeRepository;
//    private final CourseThemeCompletionRepository courseThemeCompletionRepository;
//    private final HomeworkRepository homeworkRepository;

    @Transactional(readOnly = true)
    public void validateCourseMarkRequest(MarkDto courseMark) {
//        if (!explorerRepository.existsById(courseMark.getExplorerId()))
//            throw new ExplorerNotFoundException(courseMark.getExplorerId());
//        if (isNotKeeperForThisExplorer(courseMark.getExplorerId()))
//            throw new DifferentKeeperException();
//        if (courseMark.getValue() < 1 || courseMark.getValue() > 5)
//            throw new UnexpectedMarkValueException();
//        if (!explorerNeedFinalAssessment(courseMark.getExplorerId()))
//            throw new ExplorerDoesNotNeedMarkException(courseMark.getExplorerId());
    }

//    private boolean isNotKeeperForThisExplorer(Integer explorerId) {
//        final Person authenticatedPerson = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Explorer explorer = explorerRepository.getReferenceById(explorerId);
//        Keeper keeper = keeperRepository.getKeeperForExplorer(explorer.getExplorerId());
//        return !authenticatedPerson.getPersonId().equals(keeper.getPersonId());
//    }

//    private boolean explorerNeedFinalAssessment(Integer explorerId) {
//        PersonDto person = (PersonDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return explorerRepository
//                .getExplorersNeededFinalAssessmentByKeeperPersonId(person.getPersonId()).stream()
//                .anyMatch(e -> e.getExplorerId().equals(explorerId));
//    }

    @Transactional(readOnly = true)
    public void validateThemeMarkRequest(Integer themeId, MarkDto mark) {
//        Explorer explorer = explorerRepository.findById(mark.getExplorerId())
//                .orElseThrow(() -> new ExplorerNotFoundException(mark.getExplorerId()));
//        if (isNotKeeperForThisExplorer(explorer.getExplorerId()))
//            throw new DifferentKeeperException();
//        if (mark.getValue() < 1 || mark.getValue() > 5)
//            throw new UnexpectedMarkValueException();
//        Optional<CourseThemeCompletion> courseThemeProgressOptional = courseThemeCompletionRepository
//                .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), themeId);
//        if (courseThemeProgressOptional.isPresent())
//            throw new ThemeAlreadyCompletedException(courseThemeProgressOptional.get().getCourseThemeId());
//        Integer currentThemeId = getCurrentCourseThemeId(explorer);
//        if (!currentThemeId.equals(themeId))
//            throw new UnexpectedCourseThemeException(themeId, currentThemeId);
//        if (homeworkNotCompleted(themeId, explorer))
//            throw new HomeworkNotCompletedException(themeId);
    }

//    private Integer getCurrentCourseThemeId(Explorer explorer) {
//        List<CourseThemeCompletedDto> themesProgress = getThemesProgress(explorer).getThemesWithProgress();
//        for (CourseThemeCompletedDto theme : themesProgress) {
//            if (!theme.getCompleted())
//                return theme.getCourseThemeId();
//        }
//        return themesProgress.get(themesProgress.size() - 1).getCourseThemeId();
//    }

//    private CourseWithThemesProgressDto getThemesProgress(Explorer explorer) {
//        Integer courseId = explorerGroupRepository
//                .getReferenceById(explorer.getGroupId()).getCourseId();
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new CourseNotFoundException(courseId));
//        List<CourseThemeCompletedDto> themesCompletion = new ArrayList<>();
//        for (CourseTheme ct : courseThemeRepository.findCourseThemesByCourseIdOrderByCourseThemeNumberAsc(courseId)) {
//            Boolean themeCompleted = courseThemeCompletionRepository
//                    .findCourseThemeProgressByExplorerIdAndCourseThemeId(explorer.getExplorerId(), ct.getCourseThemeId()).isPresent();
//            themesCompletion.add(
//                    new CourseThemeCompletedDto(ct.getCourseThemeId(), ct.getTitle(), themeCompleted)
//            );
//        }
//        return CourseWithThemesProgressDto.builder()
//                .courseId(courseId)
//                .title(course.getTitle())
//                .themesWithProgress(themesCompletion)
//                .build();
//    }
//
//    private boolean homeworkNotCompleted(Integer themeId, Explorer explorer) {
//        return !(homeworkRepository.findAllByCourseThemeId(themeId).size() ==
//                homeworkRepository.findAllCompletedByThemeId(themeId, explorer.getExplorerId()).size());
//    }
}
