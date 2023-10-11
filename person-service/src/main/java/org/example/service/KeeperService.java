package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.keeper.CreateKeeperDto;
import org.example.exception.classes.keeperEX.KeeperNotFoundException;
import org.example.model.Keeper;
import org.example.repository.KeeperRepository;
import org.example.service.validator.KeeperValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeeperService {
    private final KeeperRepository keeperRepository;

    private final PersonService personService;
    private final KeeperValidatorService keeperValidatorService;

    @Transactional(readOnly = true)
    public Keeper findKeeperByKeeperId(Integer keeperId) {
        return keeperRepository.findById(keeperId)
                .orElseThrow(KeeperNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Keeper findKeeperByPersonIdAndCourseId(Integer personId, Integer courseId) {
        return keeperRepository.findKeeperByPersonIdAndCourseId(personId, courseId)
                .orElseThrow(KeeperNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByPersonId(Integer personId) {
        keeperValidatorService.validateKeepersByPersonIdRequest(personId);
        return keeperRepository.findKeepersByPersonId(personId);
    }

    @Transactional(readOnly = true)
    public List<Keeper> findKeepersByCourseId(Integer courseId) {
        keeperValidatorService.validateKeepersByCourseIdRequest(courseId);
        return keeperRepository.findKeepersByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public Map<Integer, List<Keeper>> findKeepersByPersonIdIn(List<Integer> personIds) {
        return personIds.stream()
                .collect(Collectors.toMap(
                        pId -> pId,
                        this::findKeepersByPersonId
                ));
    }

    @Transactional(readOnly = true)
    public Map<Integer, Keeper> findKeepersByKeeperIdIn(List<Integer> keeperIds) {
        return keeperRepository.findKeepersByKeeperIdIn(keeperIds)
                .stream()
                .collect(
                        Collectors.toMap(
                                Keeper::getKeeperId,
                                k -> k
                        )
                );
    }

    @Transactional
    public Keeper setKeeperToCourse(Integer courseId, CreateKeeperDto createKeeper) {
        keeperValidatorService.validateSetKeeperRequest(courseId, createKeeper);
        personService.setDefaultExplorersValueForPerson(createKeeper.getPersonId());
        return keeperRepository.save(
                new Keeper(courseId, createKeeper.getPersonId())
        );
    }
}