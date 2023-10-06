package org.example.dto.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class CoursesStateDto {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String patronymic;
    @JsonProperty("openedSystems")
    private Set<Integer> openedCourses;
    @JsonProperty("studiedSystems")
    private Set<CourseWithProgressDto> studiedCourses;
    @JsonProperty("closedSystems")
    private Set<Integer> closedCourses;
}