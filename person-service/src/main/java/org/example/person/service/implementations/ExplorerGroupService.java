package org.example.person.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.grpc.ExplorerGroupsService;
import org.example.person.exception.explorer.ExplorerGroupNotFoundException;
import org.example.person.model.ExplorerGroup;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.service.api.validator.ExplorerGroupValidatorService;
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
@Slf4j
public class ExplorerGroupService {
    private final ExplorerGroupRepository explorerGroupRepository;

    private final ExplorerGroupValidatorService explorerGroupValidatorService;

    @Cacheable(cacheNames = "explorerGroupByIdCache", key = "#groupId")
    @Transactional(readOnly = true)
    public ExplorerGroup findGroupById(Long groupId) {
        return explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    log.warn("explorer group not found by id {}", groupId);
                    return new ExplorerGroupNotFoundException(groupId);
                });
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
    public ExplorerGroup createExplorerGroup(String authorizationHeader, ExplorerGroupsService.CreateGroupRequest group) {
        explorerGroupValidatorService.validateCreateExplorerGroupRequest(authorizationHeader, group);
        return explorerGroupRepository.save(
                new ExplorerGroup(
                        group.getCourseId(),
                        group.getKeeperId(),
                        Collections.emptyList()
                )
        );
    }
}
