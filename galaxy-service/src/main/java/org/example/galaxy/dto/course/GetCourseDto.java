package org.example.galaxy.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.galaxy.dto.person.PersonWithRatingDto;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCourseDto {
    private CourseDto course;
    private List<PersonWithRatingDto> explorers;
    private List<PersonWithRatingDto> keepers;
}
