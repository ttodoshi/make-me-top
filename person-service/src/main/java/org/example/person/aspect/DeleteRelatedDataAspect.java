package org.example.person.aspect;

import org.aspectj.lang.annotation.*;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.model.Keeper;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.KeeperRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Aspect
@Component
public class DeleteRelatedDataAspect {
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    private final KafkaTemplate<Long, Long> deleteProgressAndMarkByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteFeedbackByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate;

    public DeleteRelatedDataAspect(
            KeeperRepository keeperRepository, ExplorerGroupRepository explorerGroupRepository,
            @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteProgressAndMarkByExplorerIdKafkaTemplate,
            @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteFeedbackByExplorerIdKafkaTemplate,
            @Qualifier("deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate") KafkaTemplate<Long, Long> deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate) {
        this.keeperRepository = keeperRepository;
        this.explorerGroupRepository = explorerGroupRepository;
        this.deleteProgressAndMarkByExplorerIdKafkaTemplate = deleteProgressAndMarkByExplorerIdKafkaTemplate;
        this.deleteFeedbackByExplorerIdKafkaTemplate = deleteFeedbackByExplorerIdKafkaTemplate;
        this.deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate = deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.person.service.KeeperService.deleteKeepersByCourseId(..)) " +
            "&& args(courseId)", argNames = "courseId")
    public void deleteDataRelatedToKeeperPointcut(Long courseId) {
    }

    @Before(value = "deleteDataRelatedToKeeperPointcut(courseId)", argNames = "courseId")
    public void deleteDataRelatedToKeeperBeforeKeeperDeletion(Long courseId) {
        explorerGroupRepository.findExplorerGroupsByKeeperIdIn(
                keeperRepository.findKeepersByCourseId(
                        courseId
                ).stream().map(Keeper::getKeeperId).collect(Collectors.toList())
        ).forEach(g ->
                g.getExplorers().forEach(e ->
                        deleteExplorerRelatedData(e.getExplorerId())
                )
        );
    }

    @Pointcut(value = "execution(* org.example.person.service.ExplorerService.deleteExplorerById(..)) " +
            "&& args(explorerId)", argNames = "explorerId")
    public void deleteDataRelatedToExplorerPointcut(Long explorerId) {
    }

    @AfterReturning(value = "deleteDataRelatedToExplorerPointcut(explorerId)", argNames = "explorerId")
    public void deleteDataRelatedToExplorerBeforeExplorerDeletion(Long explorerId) {
        deleteExplorerRelatedData(explorerId);
    }

    private void deleteExplorerRelatedData(Long explorerId) {
        deleteProgressAndMarkByExplorerIdKafkaTemplate.send(
                "deleteProgressAndMarkTopic",
                explorerId);
        deleteFeedbackByExplorerIdKafkaTemplate.send(
                "deleteFeedbackTopic",
                explorerId
        );
    }

    @Pointcut(value = "execution(* org.example.person.service.KeeperService.setKeeperToCourse(..)) " +
            "&& args(courseId, keeper)", argNames = "courseId, keeper")
    public void deleteCourseRegistrationRequestIfPresentPointcut(Long courseId, CreateKeeperDto keeper) {
    }

    @AfterReturning(value = "deleteCourseRegistrationRequestIfPresentPointcut(courseId, keeper)", argNames = "courseId, keeper")
    public void deleteCourseRegistrationRequestIfExistsBeforeKeeperCreation(Long courseId, CreateKeeperDto keeper) {
        deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate.send(
                "deleteCourseRegistrationRequestIfPresent",
                courseId,
                keeper.getPersonId()
        );
    }
}
