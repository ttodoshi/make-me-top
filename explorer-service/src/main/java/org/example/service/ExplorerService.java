package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.event.ExplorerCreateEvent;
import org.example.dto.message.MessageDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Explorer;
import org.example.repository.ExplorerRepository;
import org.example.service.validator.ExplorerValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplorerService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerValidatorService explorerValidatorService;

    private final ModelMapper mapper;

    public Explorer findExplorerById(Integer explorerId) {
        return explorerRepository.findById(explorerId)
                .orElseThrow(ExplorerNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Explorer> findExplorersByExplorerIdIn(List<Integer> explorerIds) {
        return explorerIds.stream()
                .collect(Collectors.toMap(
                        eId -> eId,
                        this::findExplorerById
                ));
    }

    @Transactional(readOnly = true)
    public Explorer findExplorerByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return explorerRepository.findExplorerByPersonIdAndGroup_CourseId(personId, courseId)
                .orElseThrow(ExplorerNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByPersonId(Integer personId) {
        explorerValidatorService.validateGetExplorersByPersonIdRequest(personId);
        return explorerRepository.findExplorersByPersonId(personId);
    }

    @Transactional(readOnly = true)
    public List<Explorer> findExplorersByCourseId(Integer courseId) {
        explorerValidatorService.validateGetExplorersByCourseIdRequest(courseId);
        return explorerRepository.findExplorersByGroup_CourseId(courseId);
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<Explorer>> findExplorersByPersonIdIn(List<Integer> personIds) {
        return personIds.stream()
                .collect(Collectors.toMap(
                        pId -> pId,
                        this::findExplorersByPersonId
                ));
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<Explorer>> findExplorersByGroup_CourseIdIn(List<Integer> courseIds) {
        return courseIds.stream()
                .collect(Collectors.toMap(
                        cId -> cId,
                        explorerRepository::findExplorersByGroup_CourseId
                ));
    }

    @KafkaListener(topics = "explorerTopic", containerFactory = "explorerKafkaListenerContainerFactory")
    public Explorer createExplorer(ExplorerCreateEvent explorer) {
        return explorerRepository.save(
                mapper.map(explorer, Explorer.class)
        );
    }

    @Transactional
    public MessageDto deleteExplorerById(Integer explorerId) {
        explorerValidatorService.validateDeleteExplorerByIdRequest(explorerId);
        explorerRepository.deleteById(explorerId);
        return new MessageDto("Вы ушли с курса");
    }
}
