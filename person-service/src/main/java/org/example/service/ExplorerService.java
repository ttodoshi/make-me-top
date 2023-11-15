package org.example.service;

import org.example.dto.event.ExplorerCreateEvent;
import org.example.dto.explorer.ExplorerBasicInfoDto;
import org.example.dto.message.MessageDto;
import org.example.dto.person.PersonWithRatingDto;
import org.example.exception.classes.explorerEX.ExplorerNotFoundException;
import org.example.model.Explorer;
import org.example.repository.ExplorerRepository;
import org.example.service.validator.ExplorerValidatorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExplorerService {
    private final ExplorerRepository explorerRepository;

    private final ExplorerValidatorService explorerValidatorService;
    private final RatingService ratingService;

    private final KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate;
    private final ModelMapper mapper;

    public ExplorerService(ExplorerRepository explorerRepository, ExplorerValidatorService explorerValidatorService,
                           RatingService ratingService,
                           @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate") KafkaTemplate<Integer, Integer> deleteProgressAndMarkByExplorerIdKafkaTemplate,
                           @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate") KafkaTemplate<Integer, Integer> deleteFeedbackByExplorerIdKafkaTemplate,
                           ModelMapper mapper) {
        this.explorerRepository = explorerRepository;
        this.explorerValidatorService = explorerValidatorService;
        this.ratingService = ratingService;
        this.deleteProgressAndMarkByExplorerIdKafkaTemplate = deleteProgressAndMarkByExplorerIdKafkaTemplate;
        this.deleteFeedbackByExplorerIdKafkaTemplate = deleteFeedbackByExplorerIdKafkaTemplate;
        this.mapper = mapper;
    }

    @Cacheable(cacheNames = "explorerByIdCache", key = "#explorerId")
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Map<Integer, List<PersonWithRatingDto>> findExplorersWithCourseIds() {
        List<ExplorerBasicInfoDto> explorers = explorerRepository.findAll()
                .stream()
                .map(e -> new ExplorerBasicInfoDto(
                        e.getPersonId(),
                        e.getPerson().getFirstName(),
                        e.getPerson().getLastName(),
                        e.getPerson().getPatronymic(),
                        e.getExplorerId(),
                        e.getGroup().getCourseId(),
                        e.getGroupId()
                )).collect(Collectors.toList());
        Map<Integer, Double> peopleRating = ratingService.getPeopleRatingAsExplorerByPersonIdIn(
                explorers.stream()
                        .map(ExplorerBasicInfoDto::getPersonId)
                        .distinct()
                        .collect(Collectors.toList())
        );
        return explorers
                .stream()
                .collect(Collectors.groupingBy(
                        ExplorerBasicInfoDto::getCourseId,
                        Collectors.mapping(e -> new PersonWithRatingDto(
                                e.getPersonId(),
                                e.getFirstName(),
                                e.getLastName(),
                                e.getPatronymic(),
                                peopleRating.get(e.getPersonId())
                        ), Collectors.toList())
                ));
    }

    @KafkaListener(topics = "explorerTopic", containerFactory = "createExplorerKafkaListenerContainerFactory")
    @CacheEvict(cacheNames = "explorerExistsCache", key = "#result.explorerId")
    @Transactional
    public Explorer createExplorer(ExplorerCreateEvent explorer) {
        return explorerRepository.save(
                mapper.map(explorer, Explorer.class)
        );
    }

    @CacheEvict(cacheNames = {"explorerByIdCache", "explorerExistsCache"}, key = "#explorerId")
    @Transactional
    public MessageDto deleteExplorerById(Integer explorerId) {
        explorerValidatorService.validateDeleteExplorerByIdRequest(explorerId);
        explorerRepository.deleteById(explorerId);
        deleteExplorerRelatedData(explorerId);
        return new MessageDto("Вы ушли с курса");
    }

    public void deleteExplorerRelatedData(Integer explorerId) {
        deleteProgressAndMarkByExplorerIdKafkaTemplate.send(
                "deleteProgressAndMarkTopic",
                explorerId);
        deleteFeedbackByExplorerIdKafkaTemplate.send(
                "deleteFeedbackTopic",
                explorerId
        );
    }
}
