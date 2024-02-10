package org.example.progress.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.feedback.dto.event.CourseRatingOfferCreateEvent;
import org.example.feedback.dto.event.ExplorerFeedbackOfferCreateEvent;
import org.example.feedback.dto.event.KeeperFeedbackOfferCreateEvent;
import org.example.grpc.ExplorerGroupsService;
import org.example.grpc.ExplorersService;
import org.example.progress.dto.mark.MarkDto;
import org.example.progress.service.ExplorerGroupService;
import org.example.progress.service.ExplorerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeedbackOfferAspect {
    private final KafkaTemplate<Long, Object> createCourseRatingOfferKafkaTemplate;
    private final KafkaTemplate<Long, Object> createExplorerFeedbackOfferKafkaTemplate;
    private final KafkaTemplate<Long, Object> createKeeperFeedbackOfferKafkaTemplate;

    private final ExplorerService explorerService;
    private final ExplorerGroupService explorerGroupService;

    public FeedbackOfferAspect(
            @Qualifier("createCourseRatingOfferKafkaTemplate") KafkaTemplate<Long, Object> createCourseRatingOfferKafkaTemplate,
            @Qualifier("createExplorerFeedbackOfferKafkaTemplate") KafkaTemplate<Long, Object> createExplorerFeedbackOfferKafkaTemplate,
            @Qualifier("createKeeperFeedbackOfferKafkaTemplate") KafkaTemplate<Long, Object> createKeeperFeedbackOfferKafkaTemplate,
            ExplorerService explorerService,
            ExplorerGroupService explorerGroupService) {
        this.createCourseRatingOfferKafkaTemplate = createCourseRatingOfferKafkaTemplate;
        this.createExplorerFeedbackOfferKafkaTemplate = createExplorerFeedbackOfferKafkaTemplate;
        this.createKeeperFeedbackOfferKafkaTemplate = createKeeperFeedbackOfferKafkaTemplate;
        this.explorerService = explorerService;
        this.explorerGroupService = explorerGroupService;
    }

    @Pointcut(value = "execution(* org.example.progress.service.MarkService.setCourseMark(..)) " +
            "&& args(authorizationHeader, authenticatedPersonId, mark)", argNames = "authorizationHeader, authenticatedPersonId, mark")
    public void createFeedbackOfferPointcut(String authorizationHeader, Long authenticatedPersonId, MarkDto mark) {
    }

    @AfterReturning(value = "createFeedbackOfferPointcut(authorizationHeader, authenticatedPersonId, mark)", argNames = "authorizationHeader, authenticatedPersonId, mark")
    public void createFeedbackOfferPointcutAfterSendingCourseMark(String authorizationHeader, Long authenticatedPersonId, MarkDto mark) {
        ExplorersService.Explorer explorer = explorerService.findById(authorizationHeader, mark.getExplorerId());
        ExplorerGroupsService.ExplorerGroup group = explorerGroupService.findById(
                authorizationHeader, explorer.getGroupId()
        );

        createCourseRatingOfferKafkaTemplate.send(
                "createCourseRatingOfferTopic",
                new CourseRatingOfferCreateEvent(
                        explorer.getExplorerId()
                )
        );
        createExplorerFeedbackOfferKafkaTemplate.send(
                "createExplorerFeedbackOfferTopic",
                new ExplorerFeedbackOfferCreateEvent(
                        group.getKeeperId(),
                        explorer.getExplorerId()
                )
        );
        createKeeperFeedbackOfferKafkaTemplate.send(
                "createKeeperFeedbackOfferTopic",
                new KeeperFeedbackOfferCreateEvent(
                        explorer.getExplorerId(),
                        group.getKeeperId()
                )
        );
    }
}
