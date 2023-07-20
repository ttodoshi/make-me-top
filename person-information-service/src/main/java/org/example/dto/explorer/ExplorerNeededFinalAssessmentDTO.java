package org.example.dto.explorer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExplorerNeededFinalAssessmentDTO extends ExplorerRequest {
    private Integer explorerId;

    public ExplorerNeededFinalAssessmentDTO(Integer personId, String firstName, String lastName, String patronymic, Integer courseId, String title, Integer explorerId) {
        super(personId, firstName, lastName, patronymic, courseId, title);
        this.explorerId = explorerId;
    }
}
