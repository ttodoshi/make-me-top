package org.example.dto.explorer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerNeededFinalAssessmentDto extends ExplorerRequestDto {
    private Integer explorerId;

    public ExplorerNeededFinalAssessmentDto(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String courseTitle, Integer explorerId) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.explorerId = explorerId;
    }
}
