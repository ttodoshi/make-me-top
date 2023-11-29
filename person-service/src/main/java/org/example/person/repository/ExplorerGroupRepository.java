package org.example.person.repository;

import org.example.person.model.ExplorerGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExplorerGroupRepository extends JpaRepository<ExplorerGroup, Long> {
    List<ExplorerGroup> findExplorerGroupsByKeeperId(Long keeperId);

    List<ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Long> groupIds);

    List<ExplorerGroup> findExplorerGroupsByKeeperIdIn(List<Long> keeperIds);
}
