package org.example.person.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestsForKeeperDto;
import org.example.person.dto.courserequest.GetApprovedCourseRegistrationRequestsForKeeperDto;
import org.example.person.dto.explorer.CurrentKeeperGroupDto;
import org.example.person.dto.explorer.ExplorerNeededFinalAssessmentDto;
import org.example.person.dto.feedback.offer.KeeperFeedbackOfferProfileDto;
import org.example.person.dto.homework.GetHomeworkRequestDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class KeeperProfileDto extends PersonProfileDto {
    private Long totalExplorers;
    private List<CourseWithRatingDto> systems;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CurrentKeeperGroupDto currentGroup;
    private List<KeeperFeedbackOfferProfileDto> keeperFeedbacks;
    private List<CourseRegistrationRequestsForKeeperDto> studyRequests;
    private List<GetApprovedCourseRegistrationRequestsForKeeperDto> approvedRequests;
    private List<ExplorerNeededFinalAssessmentDto> finalAssessments;
    private List<GetHomeworkRequestDto> reviewRequests;
}
