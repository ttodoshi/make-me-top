package org.example.person.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.person.dto.course.CourseWithRatingDto;
import org.example.person.dto.person.GetPersonDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonProfileDto {
    private GetPersonDto person;
    private Double rating;
    private Integer totalSystems;
}
