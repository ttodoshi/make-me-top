package org.example.service;

import org.example.dto.explorer.CreateExplorerGroupDto;
import org.example.exception.classes.explorerEX.ExplorerGroupNotFoundException;
import org.example.model.ExplorerGroup;
import org.example.repository.ExplorerGroupRepository;
import org.example.service.validator.ExplorerGroupValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExplorerGroupService {
    private final ExplorerGroupRepository explorerGroupRepository;

    private final ExplorerGroupValidatorService explorerGroupValidatorService;

    private final KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate;
    private final ModelMapper mapper;

    public ExplorerGroupService(ExplorerGroupRepository explorerGroupRepository,
                                ExplorerGroupValidatorService explorerGroupValidatorService,
                                @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate") KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate,
                                @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate") KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate,
                                ModelMapper mapper) {
        this.explorerGroupRepository = explorerGroupRepository;
        this.explorerGroupValidatorService = explorerGroupValidatorService;
        this.deleteProgressAndMarkByExplorerIdKafkaTemplate = deleteProgressAndMarkByExplorerIdKafkaTemplate;
        this.deleteFeedbackByExplorerIdKafkaTemplate = deleteFeedbackByExplorerIdKafkaTemplate;
        this.mapper = mapper;
    }

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
    public ExplorerGroup createExplorerGroup(CreateExplorerGroupDto group) {
        explorerGroupValidatorService.validateCreateExplorerGroupRequest(group);
        return explorerGroupRepository.save(
                mapper.map(group, ExplorerGroup.class)
        );
    }

    @KafkaListener(topics = "deleteGroupsTopic", containerFactory = "deleteGroupsKafkaListenerContainerFactory")
    @Transactional
    public void deleteGroupsByCourseId(Integer courseId) {
        explorerGroupRepository.findExplorerGroupsByCourseId(courseId)
                .forEach(g -> g.getExplorers()
                        .forEach(e -> {
                                    deleteProgressAndMarkByExplorerIdKafkaTemplate.send(
                                            "deleteProgressAndMarkTopic",
                                            e.getExplorerId());
                                    deleteFeedbackByExplorerIdKafkaTemplate.send(
                                            "deleteFeedbackTopic",
                                            e.getExplorerId()
                                    );
                                }
                        )
                );
        explorerGroupRepository.deleteExplorerGroupsByCourseId(courseId);
    }
}
