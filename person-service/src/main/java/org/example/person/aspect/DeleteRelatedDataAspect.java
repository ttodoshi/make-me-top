package org.example.person.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.person.dto.keeper.CreateKeeperDto;
import org.example.person.model.Keeper;
import org.example.person.repository.ExplorerGroupRepository;
import org.example.person.repository.KeeperRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Aspect
@Component
public class DeleteRelatedDataAspect {
    private final KeeperRepository keeperRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    private final KafkaTemplate<Long, Long> deleteHomeworkRequestByExplorerIdKafkaTemplate;

    private final KafkaTemplate<Long, Long> deleteProgressAndMarkByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteFeedbackByExplorerIdKafkaTemplate;
    private final KafkaTemplate<Long, Long> deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate;

    public DeleteRelatedDataAspect(
            KeeperRepository keeperRepository, ExplorerGroupRepository explorerGroupRepository,
            @Qualifier("deleteHomeworkRequestByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteHomeworkRequestByExplorerIdKafkaTemplate,
            @Qualifier("deleteProgressAndMarkByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteProgressAndMarkByExplorerIdKafkaTemplate,
            @Qualifier("deleteFeedbackByExplorerIdKafkaTemplate") KafkaTemplate<Long, Long> deleteFeedbackByExplorerIdKafkaTemplate,
            @Qualifier("deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate") KafkaTemplate<Long, Long> deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate) {
        this.keeperRepository = keeperRepository;
        this.explorerGroupRepository = explorerGroupRepository;
        this.deleteHomeworkRequestByExplorerIdKafkaTemplate = deleteHomeworkRequestByExplorerIdKafkaTemplate;
        this.deleteProgressAndMarkByExplorerIdKafkaTemplate = deleteProgressAndMarkByExplorerIdKafkaTemplate;
        this.deleteFeedbackByExplorerIdKafkaTemplate = deleteFeedbackByExplorerIdKafkaTemplate;
        this.deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate = deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate;
    }

    @Pointcut(value = "execution(* org.example.person.service.implementations.KeeperService.deleteKeepersByCourseId(..)) " +
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

    @Pointcut(value = "execution(* org.example.person.service.implementations.ExplorerService.deleteExplorerById(..)) " +
            "&& args(authorizationHeader, authentication, explorerId)", argNames = "authorizationHeader, authentication, explorerId")
    public void deleteDataRelatedToExplorerPointcut(String authorizationHeader, Authentication authentication, Long explorerId) {
    }

    @AfterReturning(value = "deleteDataRelatedToExplorerPointcut(authorizationHeader, authentication, explorerId)", argNames = "authorizationHeader, authentication, explorerId")
    public void deleteDataRelatedToExplorerBeforeExplorerDeletion(String authorizationHeader, Authentication authentication, Long explorerId) {
        deleteExplorerRelatedData(explorerId);
    }

    private void deleteExplorerRelatedData(Long explorerId) {
        deleteHomeworkRequestByExplorerIdKafkaTemplate.send(
                "deleteHomeworkRequestTopic",
                explorerId
        );
        deleteProgressAndMarkByExplorerIdKafkaTemplate.send(
                "deleteProgressAndMarkTopic",
                explorerId
        );
        deleteFeedbackByExplorerIdKafkaTemplate.send(
                "deleteFeedbackTopic",
                explorerId
        );
    }

    @Pointcut(value = "execution(* org.example.person.service.implementations.KeeperService.setKeeperToCourse(..)) " +
            "&& args(authorizationHeader, courseId, keeper)", argNames = "authorizationHeader, courseId, keeper")
    public void deleteCourseRegistrationRequestIfPresentPointcut(String authorizationHeader, Long courseId, CreateKeeperDto keeper) {
    }

    @AfterReturning(value = "deleteCourseRegistrationRequestIfPresentPointcut(authorizationHeader, courseId, keeper)", argNames = "authorizationHeader, courseId, keeper")
    public void deleteCourseRegistrationRequestIfExistsBeforeKeeperCreation(String authorizationHeader, Long courseId, CreateKeeperDto keeper) {
        deleteCourseRegistrationRequestByCourseIdAndPersonIdKafkaTemplate.send(
                "deleteCourseRegistrationRequestIfPresent",
                courseId,
                keeper.getPersonId()
        );
    }
}
