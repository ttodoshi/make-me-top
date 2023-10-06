package org.example.dto.courserequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.dto.explorer.ExplorerRequestDto;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CourseRegistrationRequestForKeeperDto extends ExplorerRequestDto {
    private Integer requestId;
    private Integer keeperId;
    private Double rating;

    public CourseRegistrationRequestForKeeperDto(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer requestId, Integer keeperId, Double rating) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.requestId = requestId;
        this.keeperId = keeperId;
        this.rating = rating;
    }
}
