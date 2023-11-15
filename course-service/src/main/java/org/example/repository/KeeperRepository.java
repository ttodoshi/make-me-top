package org.example.repository;

import org.example.dto.keeper.KeeperDto;
import org.example.grpc.KeepersService;

import java.util.List;
import java.util.Optional;

public interface KeeperRepository {
    Optional<KeeperDto> findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId);

    KeepersService.KeepersByPersonIdAndGroup_CourseIdInResponse findKeepersByPersonIdAndGroupCourseIdIn(Integer personId, List<Integer> courseIds);
}
