package org.example.person.dto.explorer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerNeededFinalAssessmentDto extends ExplorerRequestDto {
    private Long explorerId;
    private Double averageMark;

    public ExplorerNeededFinalAssessmentDto(Long personId, String firstName, String lastName, String patronymic, Long courseId, String courseTitle, Long explorerId, Double averageMark) {
        super(personId, firstName, lastName, patronymic, courseId, courseTitle);
        this.explorerId = explorerId;
        this.averageMark = averageMark;
    }
}
