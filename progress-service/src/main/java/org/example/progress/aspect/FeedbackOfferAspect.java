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
import org.example.progress.exception.classes.explorer.ExplorerNotFoundException;
import org.example.progress.repository.ExplorerGroupRepository;
import org.example.progress.repository.ExplorerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeedbackOfferAspect {
    private final KafkaTemplate<Long, Object> createCourseRatingOfferKafkaTemplate;
    private final KafkaTemplate<Long, Object> createExplorerFeedbackOfferKafkaTemplate;
    private final KafkaTemplate<Long, Object> createKeeperFeedbackOfferKafkaTemplate;

    private final ExplorerRepository explorerRepository;
    private final ExplorerGroupRepository explorerGroupRepository;

    public FeedbackOfferAspect(
            @Qualifier("createCourseRatingOfferKafkaTemplate") KafkaTemplate<Long, Object> createCourseRatingOfferKafkaTemplate,
            @Qualifier("createExplorerFeedbackOfferKafkaTemplate") KafkaTemplate<Long, Object> createExplorerFeedbackOfferKafkaTemplate,
            @Qualifier("createKeeperFeedbackOfferKafkaTemplate") KafkaTemplate<Long, Object> createKeeperFeedbackOfferKafkaTemplate,
            ExplorerRepository explorerRepository,
            ExplorerGroupRepository explorerGroupRepository) {
        this.createCourseRatingOfferKafkaTemplate = createCourseRatingOfferKafkaTemplate;
        this.createExplorerFeedbackOfferKafkaTemplate = createExplorerFeedbackOfferKafkaTemplate;
        this.createKeeperFeedbackOfferKafkaTemplate = createKeeperFeedbackOfferKafkaTemplate;
        this.explorerRepository = explorerRepository;
        this.explorerGroupRepository = explorerGroupRepository;
    }

    @Pointcut(value = "execution(* org.example.progress.service.MarkService.setCourseMark(..)) " +
            "&& args(mark)", argNames = "mark")
    public void createFeedbackOfferPointcut(MarkDto mark) {
    }

    @AfterReturning(value = "createFeedbackOfferPointcut(mark)", argNames = "mark")
    public void createFeedbackOfferPointcutAfterSendingCourseMark(MarkDto mark) {
        ExplorersService.Explorer explorer = explorerRepository.findById(mark.getExplorerId())
                .orElseThrow(() -> new ExplorerNotFoundException(mark.getExplorerId()));
        ExplorerGroupsService.ExplorerGroup group = explorerGroupRepository.getReferenceById(explorer.getGroupId());

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
