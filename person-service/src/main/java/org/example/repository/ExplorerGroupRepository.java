package org.example.repository;

import org.example.model.ExplorerGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExplorerGroupRepository extends JpaRepository<ExplorerGroup, Integer> {
    List<ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Integer> groupIds);

    List<ExplorerGroup> findExplorerGroupsByKeeperIdIn(List<Integer> keeperIds);
}
