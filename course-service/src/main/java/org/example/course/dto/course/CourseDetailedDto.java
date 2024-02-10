package org.example.course.dto.course;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.course.dto.explorer.ExplorerWithRatingDto;
import org.example.course.dto.keeper.KeeperWithRatingDto;

import java.util.Collection;
import java.util.List;

@Data
public class CourseDetailedDto {
    private CourseDto course;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExplorerWithRatingDto you;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private KeeperWithRatingDto yourKeeper;
    private Collection<ExplorerWithRatingDto> explorers;
    private Collection<KeeperWithRatingDto> keepers;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer mark;

    public CourseDetailedDto(CourseDto course, Collection<ExplorerWithRatingDto> explorers, Collection<KeeperWithRatingDto> keepers) {
        this.course = course;
        this.explorers = explorers;
        this.keepers = keepers;
    }
}
