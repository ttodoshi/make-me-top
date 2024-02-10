package org.example.person.service.implementations.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.person.exception.course.CourseNotFoundException;
import org.example.person.exception.keeper.KeeperNotFoundException;
import org.example.person.service.api.course.CourseService;
import org.example.person.service.api.validator.ExplorerGroupValidatorService;
import org.example.person.service.implementations.KeeperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplorerGroupValidatorServiceImpl implements ExplorerGroupValidatorService {
    private final CourseService courseService;
    private final KeeperService keeperService;

    @Override
    @Transactional(readOnly = true)
    public void validateCreateExplorerGroupRequest(String authorizationHeader, ExplorerGroupsService.CreateGroupRequest group) {
        if (!keeperService.keeperExistsById(group.getKeeperId())) {
            log.warn("keeper by id {} not found", group.getKeeperId());
            throw new KeeperNotFoundException(group.getKeeperId());
        }
        if (!courseService.existsById(authorizationHeader, group.getCourseId())) {
            log.warn("course by id {} not found", group.getCourseId());
            throw new CourseNotFoundException(group.getCourseId());
        }
    }
}
