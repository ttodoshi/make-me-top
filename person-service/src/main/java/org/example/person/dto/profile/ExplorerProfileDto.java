package org.example.person.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.dto.courserequest.CourseRegistrationRequestForExplorerDto;
import org.example.person.dto.feedback.offer.CourseRatingOfferProfileDto;
import org.example.person.dto.feedback.offer.ExplorerFeedbackOfferProfileDto;
import org.example.person.dto.homework.GetHomeworkRequestDto;
import org.example.person.dto.person.GetPersonDto;
import org.example.person.dto.person.PersonWithGalaxiesDto;
import org.example.person.dto.progress.CurrentCourseProgressProfileDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExplorerProfileDto extends PersonProfileDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CurrentCourseProgressProfileDto currentSystem;
    private List<ExplorerFeedbackOfferProfileDto> explorerFeedbacks;
    private List<CourseRatingOfferProfileDto> courseFeedbacks;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CourseRegistrationRequestForExplorerDto studyRequest;
    private List<CourseWithRatingDto> investigatedSystems;
    private List<PersonWithGalaxiesDto> ratingTable;
    private List<GetHomeworkRequestDto> homeworkRequests;
}
