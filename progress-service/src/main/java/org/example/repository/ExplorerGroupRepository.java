package org.example.repository;

import org.example.dto.explorer.ExplorerGroupDto;

import java.util.List;
import java.util.Map;

public interface ExplorerGroupRepository {
    ExplorerGroupDto getReferenceById(Integer groupId);

    Map<Integer, Integer> findExplorerGroupsCourseIdByGroupIdIn(List<Integer> groupIds);
}
