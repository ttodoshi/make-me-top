package org.example.person.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestForKeeperWithGalaxyDto;
import org.example.person.dto.feedback.KeeperCommentDto;
import org.example.person.dto.homework.GetHomeworkRequestDto;
import org.example.person.dto.progress.CurrentCourseProgressPublicDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExplorerPublicProfileDto extends PersonProfileDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CourseRegistrationRequestForKeeperWithGalaxyDto studyRequest;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GetHomeworkRequestDto> reviewRequests;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CurrentCourseProgressPublicDto currentSystem;
    private List<CourseWithRatingDto> investigatedSystems;
    private Integer totalFeedback;
    private List<KeeperCommentDto> feedback;
}
