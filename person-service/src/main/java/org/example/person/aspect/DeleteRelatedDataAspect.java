package org.example.person.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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

    public DeleteRelatedDataAspect(
            KeeperRepository keeperRepository, ExplorerGroupRepository explorerGroupRepository,
            @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteProgressAndMarkByExplorerIdKafkaTemplate,
            @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteFeedbackByExplorerIdKafkaTemplate) {
        this.keeperRepository = keeperRepository;
        this.explorerGroupRepository = explorerGroupRepository;
        this.deleteProgressAndMarkByExplorerIdKafkaTemplate = deleteProgressAndMarkByExplorerIdKafkaTemplate;
        this.deleteFeedbackByExplorerIdKafkaTemplate = deleteFeedbackByExplorerIdKafkaTemplate;
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

    @Before(value = "deleteDataRelatedToExplorerPointcut(explorerId)", argNames = "explorerId")
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
}
