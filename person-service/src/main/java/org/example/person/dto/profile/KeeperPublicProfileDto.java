package org.example.person.dto.profile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.dto.feedback.ExplorerCommentDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class KeeperPublicProfileDto extends PersonProfileDto {
    private Long totalExplorers;
    private List<CourseWithRatingDto> systems;
    private List<ExplorerCommentDto> feedback;
}
