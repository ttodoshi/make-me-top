package org.example.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CourseRegistrationRequestDTO extends ExplorerRequest {
    public CourseRegistrationRequestDTO(Integer requestId, Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.requestId = requestId;
    }

    private Integer requestId;
}
