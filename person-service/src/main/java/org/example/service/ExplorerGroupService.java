package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.grpc.ExplorerGroupsService;
import org.example.model.ExplorerGroup;
import org.example.repository.ExplorerGroupRepository;
import org.example.service.validator.ExplorerGroupValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerGroupService {
    private final ExplorerGroupRepository explorerGroupRepository;

    private final ExplorerGroupValidatorService explorerGroupValidatorService;

    private final ModelMapper mapper;

    @Transactional(readOnly = true)
    public ExplorerGroup findGroupById(Integer groupId) {
        return explorerGroupRepository.findById(groupId)
                .orElseThrow(() -> new ExplorerGroupNotFoundException(groupId));
    }

    @Transactional(readOnly = true)
    public List<ExplorerGroup> findGroupsByKeeperIdIn(List<Integer> keeperIds) {
        return explorerGroupRepository.findExplorerGroupsByKeeperIdIn(keeperIds);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Integer> findExplorerGroupsCourseIdByGroupIdIn(List<Integer> groupIds) {
        return explorerGroupRepository.findExplorerGroupsByGroupIdIn(groupIds)
                .stream()
                .collect(Collectors.toMap(
                        ExplorerGroup::getGroupId,
                        ExplorerGroup::getCourseId
                ));
    }

    @Transactional
    public ExplorerGroup createExplorerGroup(ExplorerGroupsService.CreateGroupRequest group) {
        explorerGroupValidatorService.validateCreateExplorerGroupRequest(group);
        return explorerGroupRepository.save(
                new ExplorerGroup(
                        group.getCourseId(),
                        group.getKeeperId()
                )
        );
    }
}
