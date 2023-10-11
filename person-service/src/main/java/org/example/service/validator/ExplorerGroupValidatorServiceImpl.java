package org.example.service.validator;

import lombok.RequiredArgsConstructor;
import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.exception.classes.courseEX.CourseNotFoundException;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.repository.CourseRepository;
import org.example.repository.KeeperRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExplorerGroupValidatorServiceImpl implements ExplorerGroupValidatorService {
    private final CourseRepository courseRepository;
    private final KeeperRepository keeperRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateCreateExplorerGroupRequest(CreateExplorerGroupDto group) {
        if (!keeperRepository.existsById(group.getKeeperId()))
            throw new KeeperNotFoundException(group.getKeeperId());
        if (!courseRepository.existsById(group.getCourseId()))
            throw new CourseNotFoundException(group.getCourseId());
        // TODO у хранителя никто не учится в данный момент
    }
}
