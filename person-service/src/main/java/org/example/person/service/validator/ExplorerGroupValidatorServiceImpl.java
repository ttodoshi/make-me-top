package org.example.person.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.person.exception.classes.course.CourseNotFoundException;
import org.example.person.exception.classes.keeper.KeeperNotFoundException;
import org.example.grpc.ExplorerGroupsService;
import org.example.person.repository.CourseRepository;
import org.example.person.service.KeeperService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExplorerGroupValidatorServiceImpl implements ExplorerGroupValidatorService {
    private final CourseRepository courseRepository;
    private final KeeperService keeperService;

    @Override
    @Transactional(readOnly = true)
    public void validateCreateExplorerGroupRequest(ExplorerGroupsService.CreateGroupRequest group) {
        if (!keeperService.keeperExistsById(group.getKeeperId()))
            throw new KeeperNotFoundException(group.getKeeperId());
        if (!courseRepository.existsById(group.getCourseId()))
            throw new CourseNotFoundException(group.getCourseId());
    }
}
