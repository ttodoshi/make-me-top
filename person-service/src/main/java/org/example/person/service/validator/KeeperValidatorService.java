package org.example.person.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.exception.classes.course.CourseNotFoundException;
import org.example.person.exception.classes.explorer.PersonIsExplorerException;
import org.example.person.exception.classes.keeper.KeeperAlreadyExistsException;
import org.example.person.exception.classes.person.PersonNotFoundException;
import org.example.person.exception.classes.request.PersonHaveOpenedCourseRegistrationRequestException;
import org.example.person.repository.CourseRegistrationRequestRepository;
import org.example.person.repository.CourseRepository;
import org.example.person.repository.ExplorerRepository;
import org.example.person.repository.KeeperRepository;
import org.example.person.service.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class KeeperValidatorService {
    private final CourseRepository courseRepository;
    private final CourseRegistrationRequestRepository courseRegistrationRequestRepository;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;

    private final PersonService personService;

    @Transactional(readOnly = true)
    public void validateKeepersByPersonIdRequest(Long personId) {
        if (!personService.personExistsById(personId))
            throw new PersonNotFoundException(personId);
    }

    @Transactional(readOnly = true)
    public void validateKeepersByCourseIdRequest(Long courseId) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
    }

    @Transactional(readOnly = true)
    public void validateSetKeeperRequest(Long courseId, CreateKeeperDto request) {
        if (!courseRepository.existsById(courseId))
            throw new CourseNotFoundException(courseId);
        if (!personService.personExistsById(request.getPersonId()))
            throw new PersonNotFoundException(request.getPersonId());
        if (keeperRepository.findKeeperByPersonIdAndCourseId(request.getPersonId(), courseId).isPresent())
            throw new KeeperAlreadyExistsException(courseId);
        if (explorerRepository.findExplorerByPersonIdAndGroup_CourseId(request.getPersonId(), courseId).isPresent())
            throw new PersonIsExplorerException();
        deleteCourseRegistrationRequestIfPresent(request.getPersonId(), courseId);
    }

    private void deleteCourseRegistrationRequestIfPresent(Long personId, Long courseId) {
        // TODO
    }
}
