package org.example.person.service.implementations.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.exception.course.CourseNotFoundException;
import org.example.person.exception.keeper.KeeperAlreadyExistsException;
import org.example.person.exception.person.PersonNotFoundException;
import org.example.person.repository.ExplorerRepository;
import org.example.person.repository.KeeperRepository;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.validator.KeeperValidatorService;
import org.example.person.service.implementations.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeeperValidatorServiceImpl implements KeeperValidatorService {
    private final CourseService courseService;
    private final KeeperRepository keeperRepository;
    private final ExplorerRepository explorerRepository;

    private final PersonService personService;

    @Override
    @Transactional(readOnly = true)
    public void validateKeepersByPersonIdRequest(Long personId) {
        if (!personService.personExistsById(personId)) {
            log.warn("person by id {} not found", personId);
            throw new PersonNotFoundException(personId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateKeepersByCourseIdRequest(String authorizationHeader, Long courseId) {
        if (!courseService.existsById(authorizationHeader, courseId)) {
            log.warn("course by id {} not found", courseId);
            throw new CourseNotFoundException(courseId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateSetKeeperRequest(String authorizationHeader, Long courseId, CreateKeeperDto keeper) {
        if (!courseService.existsById(authorizationHeader, courseId)) {
            log.warn("course by id {} not found", courseId);
            throw new CourseNotFoundException(courseId);
        }
        if (!personService.personExistsById(keeper.getPersonId())) {
            log.warn("person by id {} not found", keeper.getPersonId());
            throw new PersonNotFoundException(keeper.getPersonId());
        }
        if (keeperRepository.findKeeperByPersonIdAndCourseId(keeper.getPersonId(), courseId).isPresent()) {
            log.warn("keeper with person id {} and course id {} already exists", keeper.getPersonId(), courseId);
            throw new KeeperAlreadyExistsException(courseId);
        }
    }
}
