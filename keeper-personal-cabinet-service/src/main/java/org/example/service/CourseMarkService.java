package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.coursemark.CourseMarkDTO;
import org.example.exception.classes.markEX.ExplorerDoesNotNeedMarkException;
import org.example.exception.classes.markEX.UnexpectedMarkValueException;
import org.example.model.Person;
import org.example.model.progress.CourseMark;
import org.example.repository.CourseMarkRepository;
import org.example.repository.ExplorerRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CourseMarkService {
    private final CourseMarkRepository courseMarkRepository;
    private final ExplorerRepository explorerRepository;

    public CourseMark setCourseMark(CourseMarkDTO courseMark) {
        if (courseMark.getValue() < 1 || courseMark.getValue() > 5)
            throw new UnexpectedMarkValueException();
        Person person = (Person) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean explorerNeedMark = explorerRepository.getExplorersNeededFinalAssessmentByKeeperPersonId(
                        person.getPersonId()).stream()
                .anyMatch(
                        e -> e.getExplorerId().equals(courseMark.getExplorerId()));
        if (explorerNeedMark)
            return courseMarkRepository.save(
                    new CourseMark(courseMark.getExplorerId(), new Date(), courseMark.getValue())
            );
        throw new ExplorerDoesNotNeedMarkException();
    }
}
