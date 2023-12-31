package org.example.person.service;

import lombok.RequiredArgsConstructor;
import org.example.grpc.ExplorerGroupsService;
import org.example.person.exception.classes.explorer.ExplorerGroupNotFoundException;
import org.example.person.model.ExplorerGroup;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.service.validator.ExplorerGroupValidatorService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExplorerGroupService {
    private final ExplorerGroupRepository explorerGroupRepository;

    private final ExplorerGroupValidatorService explorerGroupValidatorService;

    @Cacheable(cacheNames = "explorerGroupByIdCache", key = "#groupId")
    @Transactional(readOnly = true)
    public ExplorerGroup findGroupById(Long groupId) {
        return explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
    }

    @Cacheable(cacheNames = "explorerGroupsByKeeperIdCache", key = "#keeperId")
    @Transactional(readOnly = true)
    public List<ExplorerGroup> findExplorerGroupsByKeeperId(Long keeperId) {
        return explorerGroupRepository.findExplorerGroupsByKeeperId(keeperId);
    }

    @Cacheable(cacheNames = "explorerGroupsByKeeperIdInCache", key = "#keeperIds")
    @Transactional(readOnly = true)
    public List<ExplorerGroup> findExplorerGroupsByKeeperIdIn(List<Long> keeperIds) {
        return explorerGroupRepository.findExplorerGroupsByKeeperIdIn(keeperIds);
    }

    @Cacheable(cacheNames = "explorerGroupsByGroupIdInCache", key = "#groupIds")
    @Transactional(readOnly = true)
    public List<ExplorerGroup> findExplorerGroupsByGroupIdIn(List<Long> groupIds) {
        return explorerGroupRepository.findExplorerGroupsByGroupIdIn(groupIds);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "explorerGroupsByKeeperIdCache", key = "#group.keeperId"),
            @CacheEvict(cacheNames = "explorerGroupsByKeeperIdInCache", allEntries = true),
            @CacheEvict(cacheNames = "explorerGroupsByGroupIdInCache", allEntries = true),
    }, put = {
            @CachePut(cacheNames = "explorerGroupByIdCache", key = "#result.groupId")
    })
    @Transactional
    public ExplorerGroup createExplorerGroup(ExplorerGroupsService.CreateGroupRequest group) {
        explorerGroupValidatorService.validateCreateExplorerGroupRequest(group);
        return explorerGroupRepository.save(
                new ExplorerGroup(
                        group.getCourseId(),
                        group.getKeeperId(),
                        Collections.emptyList()
                )
        );
    }
}
